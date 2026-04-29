package tw.nekomimi.nekogram.translator;

import org.telegram.tgnet.TLRPC;
import org.telegram.ui.Components.TranslateAlert2;

import java.util.HashMap;
import java.util.List;

import app.nekogram.translator.BaseTranslator;
import app.nekogram.translator.DeepLTranslator;
import app.nekogram.translator.GoogleAppTranslator;
import app.nekogram.translator.MicrosoftTranslator;
import app.nekogram.translator.YandexTranslator;
import tw.nekomimi.nekogram.NekoConfig;
import tw.nekomimi.nekogram.translator.html.HTMLKeeper;

public class TextWithEntitiesTranslator implements Translator.ITranslator {

    private static final HashMap<String, TextWithEntitiesTranslator> wrappedTranslators = new HashMap<>();

    public static TextWithEntitiesTranslator of(String type) {
        return wrappedTranslators.computeIfAbsent(type, type1 -> {
            var translator = switch (type1) {
                case Translator.PROVIDER_YANDEX -> YandexTranslator.getInstance();
                case Translator.PROVIDER_DEEPL -> {
                    DeepLTranslator.setFormality(NekoConfig.deepLFormality);
                    yield DeepLTranslator.getInstance();
                }
                case Translator.PROVIDER_MICROSOFT -> MicrosoftTranslator.getInstance();
                default -> GoogleAppTranslator.getInstance();
            };
            return new TextWithEntitiesTranslator(translator);
        });
    }

    private final BaseTranslator translator;

    private TextWithEntitiesTranslator(BaseTranslator translator) {
        this.translator = translator;
    }

    @Override
    public Translator.TranslationResult translate(TLRPC.TL_textWithEntities query, String fl, String tl) throws Exception {
        if (NekoConfig.keepFormatting) {
            var html = HTMLKeeper.entitiesToHtml(query.text, query.entities, false);
            var result = translator.translate(html, null, tl);
            var textAndEntitiesTranslated = HTMLKeeper.htmlToEntities(result.translation, query.entities, false);
            return Translator.TranslationResult.of(
                    TranslateAlert2.preprocess(query, textAndEntitiesTranslated),
                    result.sourceLanguage
            );
        } else {
            var result = translator.translate(query.text, null, tl);
            return Translator.TranslationResult.of(Translator.textWithEntities(result.translation, null), result.sourceLanguage);
        }
    }

    @Override
    public boolean supportLanguage(String language) {
        return translator.supportLanguage(language);
    }

    @Override
    public List<String> getTargetLanguages() {
        return translator.getTargetLanguages();
    }
}
