package net.digger.ui.screen.charmap;

/**
 * Copyright © 2017  David Walton
 * 
 * This file is part of JScreen.
 * 
 * JScreen is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * Character translation map for Code Page 437.
 * @author walton
 */
public class CP437CharMap extends JScreenCharMap {
	/**
	 * Create a new instance of the Code Page 437 translation map.
	 */
	public CP437CharMap() {
		for (int i=0; i<256; i++) {
			charMap.put((char)i, CP437toUnicode[i]);
		}
	}
	
	/**
	 * http://www.ascii-codes.com/
	 * https://en.wikipedia.org/wiki/ASCII
	 * https://en.wikipedia.org/wiki/Code_page_437
	 */
	private final static char[] CP437toUnicode = new char[] {
	//	char		unicode		CP437 dec	CP437 hex	ASCII
		'\u0000',	// \u0000		0			0x00		NULl
		'☺',		// \u263A		1			0x01	^A	Start Of Heading
		'☻',		// \u263B		2			0x02	^B	Start of TeXt
		'♥',		// \u2665		3			0x03	^C	End of TeXt
		'♦',		// \u2666		4			0x04	^D	End Of Transmission
		'♣',		// \u2663		5			0x05	^E	ENQuiry
		'♠',		// \u2660		6			0x06	^F	ACKnowledgement
		'•',		// \u2022		7			0x07	^G	BELl				(doesn't print in TP write, but does in framewin)
		'◘',		// \u25D8		8			0x08	^H	BackSpace			(does BS in TP write, but prints in framewin)
		'○',		// \u25CB		9			0x09	^I	Horizontal Tab
		'◙',		// \u25D9		10			0x0A	^J	Line Feed			(does LF in TP write, but prints in framewin)
		'♂',		// \u2642		11			0x0B	^K	Vertical Tab
		'♀',		// \u2640		12			0x0C	^L	Form Feed
		'♪',		// \u266A		13			0x0D	^M	Carriage Return		(does CR in TP write, but prints in framewin)
		'♫',		// \u266B		14			0x0E	^N	Shift Out
		'☼',		// \u263C		15			0x0F	^O	Shift In
		 
		'►',		// \u25BA		16			0x10	^P	Data Link Escape
		'◄',		// \u25C4		17			0x11	^Q	Device Control 1
		'↕',		// \u2195		18			0x12	^R	Device Control 2
		'‼',		// \u203C		19			0x13	^S	Device Control 3
		'¶',		// \u00B6		20			0x14	^T	Device Control 4
		'§',		// \u00A7		21			0x15	^U	Negative AcKnowledgement
		'▬',		// \u25AC		22			0x16	^V	SYNchronous idle
		'↨',		// \u21A8		23			0x17	^W	End of Transmission Block
		'↑',		// \u2191		24			0x18	^X	CANcel
		'↓',		// \u2193		25			0x19	^Y	End of Medium
		'→',		// \u2192		26			0x1A	^Z	SUBstitute
		'←',		// \u2190		27			0x1B	^[	ESCape
		'∟',		// \u221F		28			0x1C	^\	File Separator
		'↔',		// \u2194		29			0x1D	^]	Group Separator
		'▲',		// \u25B2		30			0x1E	^^	Record Separator
		'▼',		// \u25BC		31			0x1F	^_	Unit Separator
		 
		'\u0020',	// \u0020		32			0x20		SPace
		'!',		// \u0021		33			0x21
		'"',		// \u0022		34			0x22
		'#',		// \u0023		35			0x23
		'$',		// \u0024		36			0x24
		'%',		// \u0025		37			0x25
		'&',		// \u0026		38			0x26
		'\'',		// \u0027		39			0x27
		'(',		// \u0028		40			0x28
		')',		// \u0029		41			0x29
		'*',		// \u002A		42			0x2A
		'+',		// \u002B		43			0x2B
		',',		// \u002C		44			0x2C
		'-',		// \u002D		45			0x2D
		'.',		// \u002E		46			0x2E
		'/',		// \u002F		47			0x2F
		 
		'0',		// \u0030		48			0x30
		'1',		// \u0031		49			0x31
		'2',		// \u0032		50			0x32
		'3',		// \u0033		51			0x33
		'4',		// \u0034		52			0x34
		'5',		// \u0035		53			0x35
		'6',		// \u0036		54			0x36
		'7',		// \u0037		55			0x37
		'8',		// \u0038		56			0x38
		'9',		// \u0039		57			0x39
		':',		// \u003A		58			0x3A
		';',		// \u003B		59			0x3B
		'<',		// \u003C		60			0x3C
		'=',		// \u003D		61			0x3D
		'>',		// \u003E		62			0x3E
		'?',		// \u003F		63			0x3F
		 
		'@',		// \u0040		64			0x40
		'A',		// \u0041		65			0x41
		'B',		// \u0042		66			0x42
		'C',		// \u0043		67			0x43
		'D',		// \u0044		68			0x44
		'E',		// \u0045		69			0x45
		'F',		// \u0046		70			0x46
		'G',		// \u0047		71			0x47
		'H',		// \u0048		72			0x48
		'I',		// \u0049		73			0x49
		'J',		// \u004A		74			0x4A
		'K',		// \u004B		75			0x4B
		'L',		// \u004C		76			0x4C
		'M',		// \u004D		77			0x4D
		'N',		// \u004E		78			0x4E
		'O',		// \u004F		79			0x4F
		 
		'P',		// \u0050		80			0x50
		'Q',		// \u0051		81			0x51
		'R',		// \u0052		82			0x52
		'S',		// \u0053		83			0x53
		'T',		// \u0054		84			0x54
		'U',		// \u0055		85			0x55
		'V',		// \u0056		86			0x56
		'W',		// \u0057		87			0x57
		'X',		// \u0058		88			0x58
		'Y',		// \u0059		89			0x59
		'Z',		// \u005A		90			0x5A
		'[',		// \u005B		91			0x5B
		'\\',		// \u005C		92			0x5C
		']',		// \u005D		93			0x5D
		'^',		// \u005E		94			0x5E
		'_',		// \u005F		95			0x5F
		 
		'`',		// \u0060		96			0x60
		'a',		// \u0061		97			0x61
		'b',		// \u0062		98			0x62
		'c',		// \u0063		99			0x63
		'd',		// \u0064		100			0x64
		'e',		// \u0065		101			0x65
		'f',		// \u0066		102			0x66
		'g',		// \u0067		103			0x67
		'h',		// \u0068		104			0x68
		'i',		// \u0069		105			0x69
		'j',		// \u006A		106			0x6A
		'k',		// \u006B		107			0x6B
		'l',		// \u006C		108			0x6C
		'm',		// \u006D		109			0x6D
		'n',		// \u006E		110			0x6E
		'o',		// \u006F		111			0x6F
		 
		'p',		// \u0070		112			0x70
		'q',		// \u0071		113			0x71
		'r',		// \u0072		114			0x72
		's',		// \u0073		115			0x73
		't',		// \u0074		116			0x74
		'u',		// \u0075		117			0x75
		'v',		// \u0076		118			0x76
		'w',		// \u0077		119			0x77
		'x',		// \u0078		120			0x78
		'y',		// \u0079		121			0x79
		'z',		// \u007A		122			0x7A
		'{',		// \u007B		123			0x7B
		'|',		// \u007C		124			0x7C
		'}',		// \u007D		125			0x7D
		'~',		// \u007E		126			0x7E
		'⌂',		// \u2302		127			0x7F	^?	DELete
		 
		'Ç',		// \u00C7		128			0x80
		'ü',		// \u00FC		129			0x81
		'é',		// \u00E9		130			0x82
		'â',		// \u00E2		131			0x83
		'ä',		// \u00E4		132			0x84
		'à',		// \u00E0		133			0x85
		'å',		// \u00E5		134			0x86
		'ç',		// \u00E7		135			0x87
		'ê',		// \u00EA		136			0x88
		'ë',		// \u00EB		137			0x89
		'è',		// \u00E8		138			0x8A
		'ï',		// \u00EF		139			0x8B
		'î',		// \u00EE		140			0x8C
		'ì',		// \u00EC		141			0x8D
		'Ä',		// \u00C4		142			0x8E
		'Å',		// \u00C5		143			0x8F
		 
		'É',		// \u00C9		144			0x90
		'æ',		// \u00E6		145			0x91
		'Æ',		// \u00C6		146			0x92
		'ô',		// \u00F4		147			0x93
		'ö',		// \u00F6		148			0x94
		'ò',		// \u00F2		149			0x95
		'û',		// \u00FB		150			0x96
		'ù',		// \u00F9		151			0x97
		'ÿ',		// \u00FF		152			0x98
		'Ö',		// \u00D6		153			0x99
		'Ü',		// \u00DC		154			0x9A
		'¢',		// \u00A2		155			0x9B
		'£',		// \u00A3		156			0x9C
		'¥',		// \u00A5		157			0x9D
		'₧',		// \u20A7		158			0x9E
		'ƒ',		// \u0192		159			0x9F
		 
		'á',		// \u00E1		160			0xA0
		'í',		// \u00ED		161			0xA1
		'ó',		// \u00F3		162			0xA2
		'ú',		// \u00FA		163			0xA3
		'ñ',		// \u00F1		164			0xA4
		'Ñ',		// \u00D1		165			0xA5
		'ª',		// \u00AA		166			0xA6
		'º',		// \u00BA		167			0xA7
		'¿',		// \u00BF		168			0xA8
		'⌐',		// \u2310		169			0xA9
		'¬',		// \u00AC		170			0xAA
		'½',		// \u00BD		171			0xAB
		'¼',		// \u00BC		172			0xAC
		'¡',		// \u00A1		173			0xAD
		'«',		// \u00AB		174			0xAE
		'»',		// \u00BB		175			0xAF
		 
		'░',		// \u2591		176			0xB0
		'▒',		// \u2592		177			0xB1
		'▓',		// \u2593		178			0xB2
		'│',		// \u2502		179			0xB3
		'┤',		// \u2524		180			0xB4
		'╡',		// \u2561		181			0xB5
		'╢',		// \u2562		182			0xB6
		'╖',		// \u2556		183			0xB7
		'╕',		// \u2555		184			0xB8
		'╣',		// \u2563		185			0xB9
		'║',		// \u2551		186			0xBA
		'╗',		// \u2557		187			0xBB
		'╝',		// \u255D		188			0xBC
		'╜',		// \u255C		189			0xBD
		'╛',		// \u255B		190			0xBE
		'┐',		// \u2510		191			0xBF
		 
		'└',		// \u2514		192			0xC0
		'┴',		// \u2534		193			0xC1
		'┬',		// \u252C		194			0xC2
		'├',		// \u251C		195			0xC3
		'─',		// \u2500		196			0xC4
		'┼',		// \u253C		197			0xC5
		'╞',		// \u255E		198			0xC6
		'╟',		// \u255F		199			0xC7
		'╚',		// \u255A		200			0xC8
		'╔',		// \u2554		201			0xC9
		'╩',		// \u2569		202			0xCA
		'╦',		// \u2566		203			0xCB
		'╠',		// \u2560		204			0xCC
		'═',		// \u2550		205			0xCD
		'╬',		// \u256C		206			0xCE
		'╧',		// \u2567		207			0xCF
		 
		'╨',		// \u2568		208			0xD0
		'╤',		// \u2564		209			0xD1
		'╥',		// \u2565		210			0xD2
		'╙',		// \u2559		211			0xD3
		'╘',		// \u2558		212			0xD4
		'╒',		// \u2552		213			0xD5
		'╓',		// \u2553		214			0xD6
		'╫',		// \u256B		215			0xD7
		'╪',		// \u256A		216			0xD8
		'┘',		// \u2518		217			0xD9
		'┌',		// \u250C		218			0xDA
		'█',		// \u2588		219			0xDB
		'▄',		// \u2584		220			0xDC
		'▌',		// \u258C		221			0xDD
		'▐',		// \u2590		222			0xDE
		'▀',		// \u2580		223			0xDF
		 
		'α',		// \u03B1		224			0xE0
		'ß',		// \u00DF		225			0xE1
		'Γ',		// \u0393		226			0xE2
		'π',		// \u03C0		227			0xE3
		'Σ',		// \u03A3		228			0xE4
		'σ',		// \u03C3		229			0xE5
		'µ',		// \u00B5		230			0xE6
		'τ',		// \u03C4		231			0xE7
		'Φ',		// \u03A6		232			0xE8
		'Θ',		// \u0398		233			0xE9
		'Ω',		// \u03A9		234			0xEA
		'δ',		// \u03B4		235			0xEB
		'∞',		// \u221E		236			0xEC
		'φ',		// \u03C6		237			0xED
		'ε',		// \u03B5		238			0xEE
		'∩',		// \u2229		239			0xEF
		 
		'≡',		// \u2261		240			0xF0
		'±',		// \u00B1		241			0xF1
		'≥',		// \u2265		242			0xF2
		'≤',		// \u2264		243			0xF3
		'⌠',		// \u2320		244			0xF4
		'⌡',		// \u2321		245			0xF5
		'÷',		// \u00F7		246			0xF6
		'≈',		// \u2248		247			0xF7
		'°',		// \u00B0		248			0xF8
		'∙',		// \u2219		249			0xF9
		'·',		// \u00B7		250			0xFA
		'√',		// \u221A		251			0xFB
		'ⁿ',		// \u207F		252			0xFC
		'²',		// \u00B2		253			0xFD
		'■',		// \u25A0		254			0xFE
		'\u00A0',	// \u00A0		255			0xFF		No-Break SPace
	};
}
