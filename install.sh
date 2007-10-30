
# this script is only for OBSearch developers
# run this script when after an update install.sh has changed


OBVERSION=0.8

mkdir target
cd target

mkdir temp

cd temp

wget http://obsearch.googlecode.com/files/obsearch-$OBVERSION-src.tar.gz

tar -xzf obsearch-$OBVERSION-src.tar.gz

cd obsearch-$OBVERSION

chmod 755 install.sh

./install.sh

wget http://obsearch.googlecode.com/files/obsearch-$OBVERSION.jar



mvn install:install-file -Dfile=obsearch-$OBVERSION.jar -DgroupId=gsoc -DartifactId=obsearch -Dversion=$OBVERSION -Dpackaging=jar -DpomFile=pom.xml

cd ..
cd ..
rm -fdr temp

cd ..




