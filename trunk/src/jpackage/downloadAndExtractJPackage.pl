#!/usr/bin/perl -w
# script that downloads rpm files from jpackage based on a pre-configured yum
# see the readme for details.

# from where we will download the JPackage files
$url = "ftp://jpackage.hmdc.harvard.edu/JPackage/1.7/generic/free/RPMS/";
# We will put the extracted class files here:
$output = "../../target/example/base/";

`mkdir -p $output`;

@packages = getAvailablePackages();

foreach my $x (@packages){
    my($pkg, $version, $crap) = split(/\s+/,$x);
    my $file = makeFileName($pkg, $version);
    processFile($file);
}


sub processFile{

    my($file) = @_;
    
    print "Going to process file: $file\n";

    $appDir = "$output/$file.jpackage";

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
    if(system("cd $appDir; rpm2cpio $file | cpio -ivd *.jar") !=0 || system("cd $appDir; rpm2cpio $file | cpio -ivd *.class") !=0){
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

		
    
}    

# returns a list of packages of jpackage 1.7 available 
# on Feb 7 2008.
sub getAvailablePackages{
# get the available package list
		my @packages =  `cat packages.txt`;

		return @packages;
}

# make the package file name based on the package name
# and the version
sub makeFileName{
    my($pkg, $version) = @_;
    $pkg =~ s/[.]noarch//g; # remove the .noarch thingy
    return "$pkg-$version.noarch.rpm";
}

# Preloaded methods go here.
# execute a shell command
sub shell {
    my($cmd) = shift;
    my $status = system($cmd);
    die "Command failed: $cmd\n" unless $status == 0;
}

