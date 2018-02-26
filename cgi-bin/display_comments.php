<html>
<head>
<title>
CS560 Programming assignment 1
</title>
<style>
html {
	margin: 0;
	padding: 0;
	height: 100%;
	width: 100%;
}
body {
	width: 900px;
	margin: auto;	background-image: url("http://www.misucell.com/data/out/9/IMG_315890.png");
}
table, th, td {
    border: 1px solid rgb(77,77,77);;
    border-collapse: collapse;
}
.intro {
	max-width: 100%;
	margin: auto;
	border: 1px solid rgb(153,153,153);
	background-color: rgb(204,204,204);
	text-align: center;
	color: rgb(77,77,77);
}
.container {
	max-width: 80%;
	margin-left: 200px;
	text-align: right;
	color: rgb(77,77,77);
	background-color: rgb(204,204,204);
}
</style>
</head>
<body>
<div class="intro">
<h1>User feedback</h1>
</div>
<div class="container"> 
<table style="width:100%">
<tr>
<th>name</th>
<th>comment</th>
</tr>
<?php
//sean whalen jt liso
//opens a file a parses name/comments into a html table

$lines = file('post_output.txt');
foreach ($lines as $line) {
	$fields = explode( '|', $line);
	echo "<tr>\n";
	foreach ($fields as $field) {
		echo "<td>" . strip_tags($field) . "</td>";
	}
	echo "</tr>\n";
}
?>
</table>
</div>
</body>
<html>
