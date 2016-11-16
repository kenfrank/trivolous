var secs;
var max = 100;
var run = true;
var ts;

function cd(m) {
	max = m;
 	secs = document.cd.secondsLeft.value; // change seconds here (always add an additional second to your total)
 	ts = new Date().getTime();
 	redo();
}

function done() {
	run = false;
	// change label to 'Submitted'
 	document.getElementById("submitBut").value = "Submitting...";
	// disable submit button
 	document.getElementById("submitBut").disabled = true;
	document.getElementById("formId").submit();
}
	
function redo() {
	if (run) {
		var now = new Date().getTime();
		var t = secs - Math.round((now - ts) / 1000);
		if (t<0) t=0;
	 	document.cd.secondsLeft.value = t; // setup additional displays here.
	 	document.getElementById("progressbar").style.width = ((t * 100)/max) + "%";
	 	if(t <= 0) { 
	 		done();
	 	} else {
 			timer = setTimeout("redo()",1000);
	 	}
	}
}

