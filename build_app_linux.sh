#!/bin/bash

JAVA_VERSION=21
MAIN_JAR="conficheck4j-21.0.25.jar"
APP_VERSION=21.0.25

echo "java home: $JAVA_HOME"
echo "project version: $PROJECT_VERSION"
echo "app version: $APP_VERSION"
echo "main JAR file: $MAIN_JAR"

# ------ SETUP DIRECTORIES AND FILES ----------------------------------------
# Remove previously generated java runtime and installers. Copy all required
# jar files into the input/libs folder.

rm -rfd ./build/java-runtime/
rm -rfd build/installer/

mkdir -p build/installer/input/libs/

cp build/libs/* build/installer/input/libs/
cp build/libs/${MAIN_JAR} build/installer/input/libs/

# ------ REQUIRED MODULES ---------------------------------------------------
# Use jlink to detect all modules that are required to run the application.
# Starting point for the jdep analysis is the set of jars being used by the
# application.

echo "detecting required modules"
detected_modules=`$JAVA_HOME/bin/jdeps \
  --multi-release ${JAVA_VERSION} \
  --ignore-missing-deps \
  --print-module-deps \
  --class-path "build/installer/input/libs/*" \
    build/classes/java/main/eu/hansolo/fx/conficheck4j/Main.class`
echo "detected modules: ${detected_modules}"


# ------ MANUAL MODULES -----------------------------------------------------
# jdk.crypto.ec has to be added manually bound via --bind-services or
# otherwise HTTPS does not work.
#
# See: https://bugs.openjdk.java.net/browse/JDK-8221674
#
# In addition we need jdk.localedata if the application is localized.
# This can be reduced to the actually needed locales via a jlink paramter,
# e.g., --include-locales=en,de.

manual_modules=jdk.crypto.ec,jdk.localedata
echo "manual modules: ${manual_modules}"

# ------ RUNTIME IMAGE ------------------------------------------------------
# Use the jlink tool to create a runtime image for our application. We are
# doing this is a separate step instead of letting jlink do the work as part
# of the jpackage tool. This approach allows for finer configuration and also
# works with dependencies that are not fully modularized, yet.

echo "creating java runtime image"
$JAVA_HOME/bin/jlink \
  --no-header-files \
  --no-man-pages  \
  --compress=2  \
  --strip-debug \
  --add-modules "${detected_modules},${manual_modules}" \
  --include-locales=en,de \
  --output build/java-runtime

# ------ PACKAGING ----------------------------------------------------------
# A loop iterates over the various packaging types supported by jpackage. In
# the end we will find all packages inside the build/installer directory.

for type in "deb" "rpm"
do
  echo "Creating installer of type ... $type"

  $JAVA_HOME/bin/jpackage \
  --type $type \
  --dest build/installer \
  --input build/installer/input/libs \
  --name ConfiCheck4J \
  --main-class eu.hansolo.fx.conficheck4j.Launcher \
  --main-jar ${MAIN_JAR} \
  --java-options -Xmx2048m \
  --java-options '--enable-preview' \
  --java-options '-Djdk.gtk.verbose=true' \
  --java-options '-Djdk.gtk.version=3' \
  --runtime-image build/java-runtime \
  --icon resources/Icon128.png \
  --linux-shortcut \
  --linux-menu-group "ConfiCheck4J" \
  --app-version ${APP_VERSION} \
  --vendor "Gerrit Grunwald" \
  --copyright "Copyright © 2025 Gerrit Grunwald" \
  --description "A tool written in Java(FX) that will help you to organize your conferences visits" \

done


# ------ CHECKSUM FILE --------------------------------------------------------
arch_name="$(uname -m)"

if [ "${arch_name}" = "aarch64" ]; then
    sha256sum "build/installer/conficheck4j_${APP_VERSION}_arm64.deb" > "build/installer/conficheck4j_${APP_VERSION}_arm64.deb.sha256"
    sha256sum "build/installer/conficheck4j-${APP_VERSION}-1.aarch64.rpm" > "build/installer/conficheck4j-${APP_VERSION}-1.aarch64.rpm.sha256"
else
    sha256sum "build/installer/conficheck4j_${APP_VERSION}_amd64.deb" > "build/installer/conficheck4j_${APP_VERSION}_amd64.deb.sha256"
    sha256sum "build/installer/conficheck4j-${APP_VERSION}-1.x86_64.rpm" > "build/installer/conficheck4j-${APP_VERSION}-1.x86_64.rpm.sha256"
fi
