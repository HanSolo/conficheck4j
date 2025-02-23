package eu.hansolo.fx.conficheck4j.fonts;

import com.itextpdf.io.font.FontProgram;
import com.itextpdf.io.font.FontProgramFactory;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import javafx.scene.text.Font;

import java.io.IOException;


public class Fonts {
    private static final String AVENIR_NEXT_LT_PRO_HEAVY_NAME;
    private static final String AVENIR_NEXT_LT_PRO_BOLD_NAME;
    private static final String AVENIR_NEXT_LT_PRO_DEMI_NAME;
    private static final String AVENIR_NEXT_LT_PRO_MEDIUM_NAME;
    private static final String AVENIR_NEXT_LT_PRO_REGULAR_NAME;
    private static final String AVENIR_NEXT_LT_PRO_LIGHT_NAME;
    private static final String NOTO_SANS_MONO_NAME;

    private static String avenirNextLtProHeavyName;
    private static String avenirNextLtProBoldName;
    private static String avenirNextLtProDemiName;
    private static String avenirNextLtProMediumName;
    private static String avenirNextLtProRegularName;
    private static String avenirNextLtProLightName;
    private static String notoSansMonoName;

    static {
        try {
            avenirNextLtProHeavyName   = Font.loadFont(Fonts.class.getResourceAsStream("/eu/hansolo/fx/conficheck4j/fonts/AvenirNextLTPro-Heavy.ttf"), 10).getName();
            avenirNextLtProBoldName    = Font.loadFont(Fonts.class.getResourceAsStream("/eu/hansolo/fx/conficheck4j/fonts/AvenirNextLTPro-Bold.ttf"), 10).getName();
            avenirNextLtProDemiName    = Font.loadFont(Fonts.class.getResourceAsStream("/eu/hansolo/fx/conficheck4j/fonts/AvenirNextLTPro-Demi.ttf"), 10).getName();
            avenirNextLtProMediumName  = Font.loadFont(Fonts.class.getResourceAsStream("/eu/hansolo/fx/conficheck4j/fonts/AvenirNextLTPro-Medium.ttf"), 10).getName();
            avenirNextLtProRegularName = Font.loadFont(Fonts.class.getResourceAsStream("/eu/hansolo/fx/conficheck4j/fonts/AvenirNextLTPro-Regular.ttf"), 10).getName();
            avenirNextLtProLightName   = Font.loadFont(Fonts.class.getResourceAsStream("/eu/hansolo/fx/conficheck4j/fonts/AvenirNextLTPro-UltLt.ttf"), 10).getName();
            notoSansMonoName           = Font.loadFont(Fonts.class.getResourceAsStream("/eu/hansolo/fx/conficheck4j/fonts/NotoSansMono-Regular.ttf"), 10).getName();
        } catch (Exception exception) { }
        AVENIR_NEXT_LT_PRO_HEAVY_NAME   = avenirNextLtProHeavyName;
        AVENIR_NEXT_LT_PRO_BOLD_NAME    = avenirNextLtProBoldName;
        AVENIR_NEXT_LT_PRO_DEMI_NAME    = avenirNextLtProDemiName;
        AVENIR_NEXT_LT_PRO_MEDIUM_NAME  = avenirNextLtProMediumName;
        AVENIR_NEXT_LT_PRO_REGULAR_NAME = avenirNextLtProRegularName;
        AVENIR_NEXT_LT_PRO_LIGHT_NAME   = avenirNextLtProLightName;
        NOTO_SANS_MONO_NAME             = notoSansMonoName;
    }


    // ******************** Methods *******************************************
    public static Font avenirNextLtProHeavy(final double size) { return new Font(AVENIR_NEXT_LT_PRO_HEAVY_NAME, size); }
    public static Font avenirNextLtProBold(final double size) { return new Font(AVENIR_NEXT_LT_PRO_BOLD_NAME, size); }
    public static Font avenirNextLtProDemi(final double size) { return new Font(AVENIR_NEXT_LT_PRO_DEMI_NAME, size); }
    public static Font avenirNextLtProMedium(final double size) { return new Font(AVENIR_NEXT_LT_PRO_MEDIUM_NAME, size); }
    public static Font avenirNextLtProRegular(final double size) { return new Font(AVENIR_NEXT_LT_PRO_REGULAR_NAME, size); }
    public static Font avenirNextLtProLight(final double size) { return new Font(AVENIR_NEXT_LT_PRO_LIGHT_NAME, size); }
    public static Font notoSansMono(final double size) { return new Font(NOTO_SANS_MONO_NAME, size); }

    public static PdfFont getAvenirNextLtHeavy() throws IOException {
        final String      name    = "/eu/hansolo/fx/conficheck4j/fonts/AvenirNextLTPro-Heavy.ttf";
        final FontProgram program = FontProgramFactory.createFont(name);
        final PdfFont     font    = PdfFontFactory.createFont(program);
        return font;
    }
    public static PdfFont getAvenirNextLtBold() throws IOException  {
        final String      name    = "/eu/hansolo/fx/conficheck4j/fonts/AvenirNextLTPro-Bold.ttf";
        final FontProgram program = FontProgramFactory.createFont(name);
        final PdfFont     font    = PdfFontFactory.createFont(program);
        return font;
    }
    public static PdfFont getAvenirNextLtDemi() throws IOException  {
        final String      name    = "/eu/hansolo/fx/conficheck4j/fonts/AvenirNextLTPro-Demi.ttf";
        final FontProgram program = FontProgramFactory.createFont(name);
        final PdfFont     font    = PdfFontFactory.createFont(program);
        return font;
    }
    public static PdfFont getAvenirNextLtMedium() throws IOException  {
        final String      name    = "/eu/hansolo/fx/conficheck4j/fonts/AvenirNextLTPro-Medium.ttf";
        final FontProgram program = FontProgramFactory.createFont(name);
        final PdfFont     font    = PdfFontFactory.createFont(program);
        return font;
    }
    public static PdfFont getAvenirNextLtRegular() throws IOException  {
        final String      name    = "/eu/hansolo/fx/conficheck4j/fonts/AvenirNextLTPro-Regular.ttf";
        final FontProgram program = FontProgramFactory.createFont(name);
        final PdfFont     font    = PdfFontFactory.createFont(program);
        return font;
    }
    public static PdfFont getAvenirNextLtLight() throws IOException  {
        final String      name    = "/eu/hansolo/fx/conficheck4j/fonts/AvenirNextLTPro-UltLt.ttf";
        final FontProgram program = FontProgramFactory.createFont(name);
        final PdfFont     font    = PdfFontFactory.createFont(program);
        return font;
    }
    public static PdfFont getNotoSansMono() throws IOException  {
        final String      name    = "/eu/hansolo/fx/conficheck4j/fonts/NotoSansMono-Regular.ttf";
        final FontProgram program = FontProgramFactory.createFont(name);
        final PdfFont     font    = PdfFontFactory.createFont(program);
        return font;
    }

}
