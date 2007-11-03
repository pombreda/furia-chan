
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


# install lucene

wget http://ftp.kddilabs.jp/infosystems/apache/lucene/java/lucene-2.2.0.tar.gz

tar -xzf lucene-2.2.0.tar.gz

cd lucene-2.2.0

mvn install:install-file -Dfile=lucene-core-2.2.0.jar -DgroupId=apache -DartifactId=lucene -Dversion=2.2.0 -Dpackaging=jar -DpomFile=pom.xml

cd ..

rm -fdr lucene-2.2.0
rm lucene-2.2.0.tar.gz


