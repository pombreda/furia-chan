   ___           _                  _                 
  / __\   _ _ __(_) __ _        ___| |__   __ _ _ __  
 / _\| | | | '__| |/ _` |_____ / __| '_ \ / _` | '_ \ 
/ /  | |_| | |  | | (_| |_____| (__| | | | (_| | | | |
\/    \__,_|_|  |_|\__,_|      \___|_| |_|\__,_|_| |_|
                                                      

Version: ${version}

*********
Synopsis:
*********

Furia-chan is a state of the art Open Source/ Libre software license violation detector (a "google" for programs). If given as input a binary program p, its output is a list of the top n closest programs to p. It works even if p is control-flow obfuscated! :) It can find embedded (stolen) components of p in a database of FLOSS programs.

*********************
Information for Users
*********************

Furia-chan is a program matcher. The matching process consist of two steps. At first, you generate fragments of a program. Think of each fragment as the words of a web document.  The next step uses OBSearch and Lucene to calculate the similarity of the program.

Currently, fragments can only be generated from Java Byte-code. There are plans to implement a program fragmentation engine for x86. Stay tuned!

* To fragment a folder of class files:

* To fragment a folder of folders of class files: 
(folder name used as app name)

Now that you have fragmented a program, we can load them and search them
with Furia-chan.

* To add a fragmented program:

* To add many fragmented programs at once:

* To find the similarity of programs:

Caveats:

Furia-chan will work fine if each query has at least 100 different
fragments. The program automatically checks for this.

If you copy the database to a computer that has different big-endianness you will break furia-chan. I am using some Java unsafe classes for performance reasons; it is much faster to re-create the database again than to wait the extra time if we don't use unsafe classes. If you want to use safe classes, you can easily change the code of furia-chan to use only safe classes.

**************************
Information for Developers
**************************

 Requirements:
 -------------

* You need a recent JDK 1.5, 1.6.
* You need to have a recent version Maven and ANT installed and working
  (We have tested OBSearch with Maven 2.0.8 and 2.0.7 and ANT 1.7.0).

Checkout furia-chan:

svn checkout https://furia-chan.googlecode.com/svn/trunk/ furia-chan --username <you>
or if you don't have an account:

svn checkout http://furia-chan.googlecode.com/svn/trunk/ furia-chan-read-only

The first time you checkout Furia-chan do a:
./install.sh 

This will download and install all the necessary dependencies.

Whenever you do svn update and install.sh is changed please
run install.sh again.


 Compiling:
 ----------

mvn compile



*******************************
Additional notes for developers
*******************************

 Using maven with Eclipse:
 -------------------------

# do this line only once
mvn -Declipse.workspace=/home/<usr>/workspace eclipse:add-maven-repo

# do this line every time you change the project's dependencies or
any major thing that could affect eclipse:

mvn eclipse:eclipse 

 Testing:
 --------

mvn test

Note:
If you want to do the 40 hour test, then replace this line
<my.test.data.db>slices-small</my.test.data.db> in pom.xml:
by:
<my.test.data.db>slices</my.test.data.db>


 Build website:
 -----------
 Builds the latest version of OBSearch's website.

mvn site


 How to make a branch:
 ---------------------

svn copy https://obsearch.googlecode.com/svn/trunk/ \
             https://obsearch.googlecode.com/svn/branches/mynewbranch \
             -m "my new branch"  --username <you>

 How to make a tag (label):
 --------------------------

svn copy https://obsearch.googlecode.com/svn/trunk/ \
             https://obsearch.googlecode.com/svn/tags/0.7-GSOC \
             -m "initial release"  --username <you>

--- 

 Packaging:
 ----------
 
 mvn assembly:assembly




 Deployment:
 -----------
Before deploying, change the line in pom.xml from
<my.test.data.db>slices-small</my.test.data.db>
to:
<my.test.data.db>slices</my.test.data.db>

And run: mvn test
The test will run for 40 hours, if everything is fine, then you can release:
but first restore the line <my.test.data.db>slices-small</my.test.data.db>
in pom.xml

perl deploy.pl

This script will generate the binary files, upload the website to
berlios.de and will also generate an announce.txt file ready to 
be sent to the mailing lists. The label creation is a manual process
 and it must be done after this script has been completed:
 
svn copy https://obsearch.googlecode.com/svn/trunk/ \
             https://obsearch.googlecode.com/svn/tags/0.7-GSOC \
             -m "initial release"  --username <you>