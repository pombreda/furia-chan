
The script downloadAndExtractJPackage.pl downloads classes from
the JPackage repository. 

The following instructions have been tested in ubuntu 7.04. With some
modifications they should work for most distributions. If you are
using windows, cygwin you should be able to follow the instructions
described below.  


Requirements:
* rpm2cpio
* cpio
* wget


In ubuntu you would have to do:
sudo apt-get install cpio wget rpm

In order to download test data, we will dump the latest versions of
the packages found in jpackage (http://www.jpackage.org/). 

perl downloadAndExtractJPackage.pl

This will leave a folder of folders of class files. Each folder is one
application. The results will be stored here:
../../target/example/base/


The script uses the file packages.txt to fetch the latest versions of
each package. If you want to update this file you have to do the
following:

* install yum
sudo apt-get install yum

sudo gedit /etc/yum.conf

Add the following:

[jpackage17-generic]
name=JPackage 1.7, generic
baseurl=ftp://jpackage.hmdc.harvard.edu/JPackage/1.7/generic/free/
gpgcheck=0

update yum:

sudo yum update

Finally create the packages.txt file:

sudo yum list all | grep -v  -e javadoc | grep -v  -e manual > packages.txt

Now you can get the latest versions of all the packages!
