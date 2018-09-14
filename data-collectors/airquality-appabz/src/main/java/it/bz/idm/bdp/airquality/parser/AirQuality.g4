grammar AirQuality;

/* Parser rules */
dataset
	: row+
	;
row 
	: station ',' decimal ',' time ',' date ',' decimal ',' blockno ',' block (',' block)* ',' terminator
	;
blockno
	: UPPERCASE decimal
	; 
station
	: 'ST' decimal 
	;
time
	: decimal '.' decimal '.' decimal
	;
date
	: decimal ',' decimal ',' decimal 
	;
terminator
	: '#' decimal ',' EOL
	;
number 
	: decimal 
	| real 
	| '*'		// measurement data for this record is not available
	;
block 
	: decimal ',' keyval (',' keyval)* 
	;
keyval 
	: UPPERCASE ',' number*
	;
decimal 
	: SIGN? DIGIT+
	;
real 
	: SIGN? '.' DIGIT+
	| SIGN? DIGIT+ '.' DIGIT+?
	;
	
	
/* Lexer rules */
SIGN 
	: [+-] 
	;
EOL
	: [\r\n]+
	| EOF
	;
DIGIT 
	: [0-9] 
	;
UPPERCASE
	: [A-Z]
	;
	
