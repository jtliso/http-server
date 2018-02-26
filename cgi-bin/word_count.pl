# sean whalen cs565 hw6 p4
sub mySort # 1 arg: hashtable ref 
{
	my $wf = $_[0];
	my @arr = ();
	foreach $key (keys(%$wf)) {
		push(@arr, $key.": ".$$wf{$key});
	}
	my @sorted = sort {
		@one = split /: /, $a;
		@two = split /: /, $b;
		if ($one[1] == $two[1]) {
			return $one[0] cmp $two[0];
		}
		else {
			return $two[1] <=> $one[1];
		}
	} @arr;
	print join("</br>", @sorted);
	print "</br>";
}

sub getFreqFromFile # 2 args: fname, hashtable ref
{
	open(INFILE, "<$_[0]") or die "Cannot open file $_[0]: $!";
	my $wf = $_[1];
	foreach $line (<INFILE>) {
		my @words = ();
		chomp($line);
		@words = split(/\s+/, $line);
		foreach $word (@words) {
			if ($word =~ /^\W*(\w+)\W*/) {
				$word = $1;
				$word = lc($word);
				if (exists($$wf{$word})) {
					$$wf{$word} += 1;
				}
				else {
					$$wf{$word} = 1;
				}
			}
		}
	}
	close(INFILE);
}


# execution starts here
%wf = ();
$fname = "post_output.txt";
#foreach $fname (@ARGV) {
	getFreqFromFile($fname, \%wf);
#}

printf "<html><body><p>";
mySort(\%wf);
printf "</p></body></html>";
