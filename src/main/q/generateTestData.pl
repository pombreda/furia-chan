#!/usr/bin/perl -w

# Generates binary programs, makes sure that no strange
# gcc flags are used. 
# This script reads all the apps from /usr/portage
# and attempts to install them, if no strange gcc flags are
# used (except the ones we chose), then we output the
# app name into the file "OK.txt".
# Apps that are ignored go to file "Failed.txt"

# If a flag is here, and in @ignoreFlags we do not
# ignore the flag and assume it to be correct
@usedFlags = ("-O2");

# flags that should be ignored
@ignoreFlags = ("-Os", "-O\s", "-O4", "-O3", "-O2", "-O5", "-O6", "-O7", "-O8", "-O9",  "-fno-default-inline", "-fno-defer-pop", "-fforce-mem", "-fforce-addr", "-fomit-frame-pointer", "-foptimize-sibling-calls", "-fno-inline", "-finline-functions", "-finline-functions-called-once", "-fearly-inlining", "-finline-limit=" , "-fkeep-inline-functions", "-fkeep-static-consts", "-fmerge-all-constants", "-fmodulo-sched", "-fno-branch-count-reg", "-fno-function-cse", "-fno-zero-initialized-in-bss", "-fbounds-check", "-fmudflap", "-fmudflapth", "-fmudflapir", "-fstrength-reduce", "-fthread-jumps", "-fcse-follow-jumps", "-fcse-skip-blocks", "-frerun-cse-after-loop", "-frerun-loop-opt", "-fgcse", "-fgcse-lm", "-fgcse-sm", "-fgcse-las", "-fgcse-after-reload", "-floop-optimize", "-floop-optimize2", "-funsafe-loop-optimizations", "-fcrossjumping", "-fif-conversion", "-fif-conversion2", "-fdelete-null-pointer-checks", "-fexpensive-optimizations", "-foptimize-register-move", "-fregmove", "-fdelayed-branch", "-fschedule-insns", "-fschedule-insns2", "-fno-sched-interblock", "-fno-sched-spec", "-fsched-spec-load", "-fsched-spec-load-dangerous", "-fsched-stalled-insns=", "-fsched-stalled-insns-dep=", "-fsched2-use-superblocks", "-fsched2-use-traces", "-freschedule-modulo-scheduled-loops", "-fcaller-saves", "-ftree-pre", "-ftree-fre", "-ftree-copy-prop", "-ftree-salias", "-ftree-sink", "-ftree-ccp", "-ftree-store-ccp", "-ftree-dce", "-ftree-dominator-opts", "-ftree-dominator-opts", "-ftree-ch", "-ftree-loop-optimize", "-ftree-loop-linear", "-ftree-loop-im", "-ftree-loop-ivcanon", "-fivopts", "-ftree-sra", "-ftree-copyrename", "-ftree-ter", "-ftree-lrs", "-ftree-vectorize", "-ftree-vect-loop-version", "-ftree-vrp", "-ftracer", "-funroll-loops", "-funroll-all-loops", "-fsplit-ivs-in-unroller", "-fvariable-expansion-in-unroller", "-fprefetch-loop-arrays", "-fno-peephole", "-fpeephole2", "-fno-guess-branch-probability", "-freorder-blocks", "-freorder-blocks-and-partition", "-freorder-functions", "-fstrict-aliasing", "-falign-functions", "-falign-functions=", "-falign-labels", "-falign-labels=", "-falign-loops=", "-falign-jumps", "-funit-at-a-time", "-fweb", "-fwhole-program", "-fno-cprop-registers", "-fprofile-generate", "-fprofile-use", "-ffloat-store", "-ffast-math", "-fno-math-errno", "-funsafe-math-optimizations", "-ffinite-math-only", "-fno-trapping-math", "-frounding-math", "-fsignaling-nans", "-fsingle-precision-constant", "-fcx-limited-range", "-fno-cx-limited-range", "-fbranch-probabilities", "-fprofile-values", "-fvpt", "-frename-registers", "-ftracer", "-funroll-loops", "-funroll-all-loops", "-fpeel-loops", "-fmove-loop-invariants", "-funswitch-loops", "-fprefetch-loop-arrays", "-ffunction-sections", "-fdata-sections", "-fbranch-target-load-optimize", "-fbranch-target-load-optimize2", "-fbtr-bb-exclusive", "-fstack-protector", "-fstack-protector-all", "salias-max-implicit-fields", "sra-max-structure-size", "sra-field-structure-ratio", "max-crossjump-edges", "min-crossjump-insns", "max-grow-copy-bb-insns", "max-goto-duplication-insns", "max-delay-slot-insn-search", "max-gcse-memory", "max-gcse-passes", "max-pending-list-length", "max-inline-insns-single", "max-inline-insns-auto", "large-function-insns", "large-function-growth", "large-unit-insns", "inline-unit-growth", "max-inline-insns-recursive", "max-inline-recursive-depth", "min-inline-recursive-probability", "inline-call-cost", "max-unrolled-insns", "max-average-unrolled-insns", "max-unroll-times", "max-peeled-insns", "max-peel-times", "max-completely-peeled-insns", "max-completely-peel-times", "max-unswitch-insns", "max-unswitch-level", "lim-expensive", "iv-consider-all-candidates-bound", "iv-max-considered-uses", "iv-always-prune-cand-set-bound", "scev-max-expr-size", "vect-max-version-checks", "max-iterations-to-track", "hot-bb-count-fraction", "hot-bb-frequency-fraction", "max-predicted-iterations", "tracer-dynamic-coverage", "tracer-dynamic-coverage-feedback", "tracer-max-code-growth", "tracer-min-branch-ratio", "tracer-min-branch-ratio", "tracer-min-branch-ratio-feedback", "max-cse-path-length", "max-cse-insns", "global-var-threshold", "max-aliased-vops", "ggc-min-expand", "ggc-min-heapsize", "max-reload-search-insns", "max-cselib-memory-location", "max-flow-memory-location", "reorder-blocks-duplicate", "reorder-blocks-duplicate-feedback", "max-sched-region-blocks", "max-sched-region-insns", "min-sched-prob", "max-last-value-rtl", "integer-share-limit", "min-virtual-mappings", "virtual-mappings-ratio", "ssp-buffer-size", "max-jump-thread-duplication-stmts", "max-fields-for-field-sensitive" );



@packages = `cd /usr/portage; find . -name "*-*.ebuild"`;

open OK, ">OK.txt" or die "Could not open OK.txt";
open BAD, ">BAD.txt" or die "Could not open BAD.txt";

#foreach my $app (@packages) {
#		processApp($app);
#} 


emergeApp("mplayer");
emergeApp("emacs");


close OK;
close BAD;

sub processApp {
		my($app) = @_;
		my ($a) = ($app =~ /\/([^\/]+)\/[^\/]+.ebuild$/);
		if(! exists $processed{$a}){
			$processed{$a} = "a";
			# $a holds an app name. 
  		print "Processing $a\n";
			# for each returned app, we check that all the 
			# returned lines do not have any weird flag.
			
			emergeApp($a);
			
		}
}

sub emergeApp{
		my($a) = @_;
		
		$cmd = "emerge $a";
		open(CMD, "$cmd |") or die "Can't run '$cmd'\n$!\n";
		$i=0;
		my $bad = 0;
		my $process =0;
		while(<CMD>){
				if(/Emerging/){
						# An emerging operation is happening.
						if($_ =~ /$a/){
								print "Processing ON: $_";
								$process=1;
						}else{
								if($process == 1){
										print "Processing OFF: $_";
										$process =0;
								}
						}
				}
				if($process && checkFlags($_)){
						print "Bad flag: $_\n";
						$bad = 1;
				}
				$_; 
		}
		if($bad){
				print BAD "$a\n";
		}else{
				print OK "$a\n";
		}
		close(CMD);
}

sub checkFlags{
		my($string) = @_;
		for my $pattern (@ignoreFlags){
				if(! isUsedFlag($pattern)){
						#check the pattern.
						if($string =~ /$pattern/){
								print "Bad flag: <$pattern>\n";
								return 1;
						}
				}
		}
		return 0;
}

sub isUsedFlag{
		my($string) = @_;
		for $s (@usedFlags){
				if($s eq $string){
						return 1;
				}
		}
		return 0;
}
