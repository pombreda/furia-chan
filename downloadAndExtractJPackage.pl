#!/usr/bin/perl -w
# script that downloads rpm files from jpackage based on a pre-configured yum
# see the readme for details.

# Set this two variables!!!
$url = "ftp://jpackage.hmdc.harvard.edu/JPackage/1.6/generic/free/RPMS/";
$output = "/home/amuller/ALMACEN/JPackage/";

@packages = getAvailablePackages();

foreach my $x (@packages){
    my($pkg, $version, $crap) = split(/\s+/,$x);
    my $file = makeFileName($pkg, $version);
    processFile($file);
}


sub processFile{

    my($file) = @_;
    
    print "Going to process file: $file\n";

    $appDir = "$output$file.jpackage";

    if(-e $appDir){
	return; # return if the directory exists
    }
    
    shell("mkdir $appDir");
    # get the file if error just skip everything
    if(system("wget -t 20 --retry-connrefused --directory-prefix=$appDir $url/$file") != 0){
	shell("rm -fdr $appDir");
	return;
    }
    
    # extract the jar files!
    if(system("cd $appDir; rpm2cpio $file | cpio -ivd *.jar") !=0){
	shell("rm -fdr $appDir");
	return;
    }

    if(system("cd $appDir; rpm2cpio $file | cpio -ivd *.class") !=0){
	shell("rm -fdr $appDir");
	return;
    }

    
    # remove the rpm file
    shell("cd $appDir; rm $file");

    # get the jar files and only regular files (no links or stuff like that)
    @jar_files = `cd $appDir; find . -name "*.jar" -type f`;
    # we will store everything here
    
    foreach $x (@jar_files){
	#unzip the jar file

	system("cd $appDir; unzip -o $x"); # if it fails, let it be.
	# delete the jar file
	shell("cd $appDir; rm $x");
    }

    # remove everything that is not a class file!
    
}    

# returns all the packages available in Jpackage!
sub getAvailablePackages{
# get the available package list
my @packages =  `sudo yum list all | grep -v  -e javadoc | grep -v  -e manual`;

# remove the first 3 elements of this array.
shift @packages;
shift @packages;
shift @packages;
shift @packages;
shift @packages;
return @packages;
}
