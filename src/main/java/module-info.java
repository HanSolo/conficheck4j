module eu.hansolo.fx.conficheck4j {
    // Java
    requires java.net.http;
    requires java.desktop;

    // Java-FX
    requires javafx.base;
    requires javafx.graphics;
    requires javafx.controls;

    // 3rd Party
    requires transitive eu.hansolo.jdktools;
    requires transitive eu.hansolo.toolbox;
    //requires transitive eu.hansolo.toolboxfx;
    requires transitive com.google.gson;

    exports eu.hansolo.fx.conficheck4j;
}