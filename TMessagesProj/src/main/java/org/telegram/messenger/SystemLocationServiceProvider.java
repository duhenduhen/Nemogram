package org.telegram.messenger;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.core.util.Consumer;

@SuppressLint("MissingPermission")
public class SystemLocationServiceProvider implements ILocationServiceProvider {

    private LocationManager locationManager;
    private ILocationListener activeListener;
    private LocationListener systemListener;

    @Override
    public void init(Context context) {
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    @Override
    public ILocationRequest onCreateLocationRequest() {
        return new ILocationRequest() {
            @Override public void setPriority(int priority) {}
            @Override public void setInterval(long interval) {}
            @Override public void setFastestInterval(long interval) {}
        };
    }

    @Override
    public IMapApiClient onCreateLocationServicesAPI(Context context, IAPIConnectionCallbacks connectionCallbacks, IAPIOnConnectionFailedListener failedListener) {
        return new IMapApiClient() {
            @Override
            public void connect() {
                connectionCallbacks.onConnected(null);
            }
            @Override
            public void disconnect() {}
        };
    }

    @Override
    public boolean checkServices() {
        return locationManager != null;
    }

    @Override
    public void getLastLocation(Consumer<Location> callback) {
        if (locationManager == null) {
            callback.accept(null);
            return;
        }
        Location best = null;
        for (String provider : locationManager.getProviders(true)) {
            Location l = locationManager.getLastKnownLocation(provider);
            if (l != null && (best == null || l.getAccuracy() < best.getAccuracy())) {
                best = l;
            }
        }
        callback.accept(best);
    }

    @Override
    public void requestLocationUpdates(ILocationRequest request, ILocationListener locationListener) {
        if (locationManager == null) return;
        activeListener = locationListener;
        systemListener = location -> locationListener.onLocationChanged(location);
        for (String provider : new String[]{
                LocationManager.GPS_PROVIDER,
                LocationManager.NETWORK_PROVIDER,
                LocationManager.PASSIVE_PROVIDER}) {
            try {
                locationManager.requestLocationUpdates(provider, 1000, 0, systemListener);
            } catch (Exception ignore) {}
        }
    }

    @Override
    public void removeLocationUpdates(ILocationListener locationListener) {
        if (locationManager == null || systemListener == null) return;
        try {
            locationManager.removeUpdates(systemListener);
        } catch (Exception ignore) {}
        systemListener = null;
        activeListener = null;
    }

    @Override
    public void checkLocationSettings(ILocationRequest request, Consumer<Integer> callback) {
        callback.accept(STATUS_SUCCESS);
    }
}