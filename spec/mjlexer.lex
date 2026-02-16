// ============================================================================
// MicroJava Lexer - JFlex Specification
// Nivo A - Osnovne funkcionalnosti
// ============================================================================

package rs.ac.bg.etf.pp1;

import java_cup.runtime.Symbol;

%%

%{
    // Ukljucivanje informacije o poziciji tokena
    private Symbol new_symbol(int type) {
        return new Symbol(type, yyline + 1, yycolumn);
    }

    // Ukljucivanje informacije o poziciji tokena sa vrednoscu
    private Symbol new_symbol(int type, Object value) {
        return new Symbol(type, yyline + 1, yycolumn, value);
    }
%}

%cup
%line
%column

%xstate COMMENT

%eofval{
    return new_symbol(sym.EOF);
%eofval}

%%

// ============================================================================
// BELI KARAKTERI (ignorisu se)
// ============================================================================

" "     { }
"\b"    { }
"\t"    { }
"\r\n"  { }
"\n"    { }
"\r"    { }
"\f"    { }

// ============================================================================
// KOMENTARI
// ============================================================================

// Jednolinijski komentar
"//" { yybegin(COMMENT); }
<COMMENT> . { yybegin(COMMENT); }
<COMMENT> "\r\n" { yybegin(YYINITIAL); }
<COMMENT> "\n" { yybegin(YYINITIAL); }

// ============================================================================
// KLJUCNE RECI
// ============================================================================

"program"   { return new_symbol(sym.PROG, yytext()); }
"enum"      { return new_symbol(sym.ENUM, yytext()); }
"const"     { return new_symbol(sym.CONST, yytext()); }
"return"    { return new_symbol(sym.RETURN, yytext()); }
"void"      { return new_symbol(sym.VOID, yytext()); }
"read"      { return new_symbol(sym.READ, yytext()); }
"print"     { return new_symbol(sym.PRINT, yytext()); }
"new"       { return new_symbol(sym.NEW, yytext()); }
"length" { return new_symbol(sym.LENGTH, yytext()); }


// Boolean konstante (moraju biti pre IDENT pravila!)
("true"|"false") { return new_symbol(sym.BOOL, yytext().equals("true")? 1 : 0); }
//"true"      { return new_symbol(sym.BOOL_CONST, Boolean.TRUE); }
//"false"     { return new_symbol(sym.BOOL_CONST, Boolean.FALSE); }

// ============================================================================
// OPERATORI (visekarakterne prvo!)
// ============================================================================

// Relacioni operatori
"=="    { return new_symbol(sym.EQ, yytext()); }
"!="    { return new_symbol(sym.NE, yytext()); }
">="    { return new_symbol(sym.GE, yytext()); }
"<="    { return new_symbol(sym.LE, yytext()); }
">"     { return new_symbol(sym.GT, yytext()); }
"<"     { return new_symbol(sym.LT, yytext()); }

// Inkrement i dekrement
"++"    { return new_symbol(sym.INC, yytext()); }
"--"    { return new_symbol(sym.DEC, yytext()); }

// Aritmeticki operatori
"+"     { return new_symbol(sym.PLUS, yytext()); }
"-"     { return new_symbol(sym.MINUS, yytext()); }
"*"     { return new_symbol(sym.MUL, yytext()); }
"/"     { return new_symbol(sym.DIV, yytext()); }
"%"     { return new_symbol(sym.MOD, yytext()); }

// Dodela
"="     { return new_symbol(sym.ASSIGN, yytext()); }

// ============================================================================
// SEPARATORI
// ============================================================================

";"     { return new_symbol(sym.SEMI, yytext()); }
","     { return new_symbol(sym.COMMA, yytext()); }
"."     { return new_symbol(sym.DOT, yytext()); }
"("     { return new_symbol(sym.LPAREN, yytext()); }
")"     { return new_symbol(sym.RPAREN, yytext()); }
"["     { return new_symbol(sym.LBRACKET, yytext()); }
"]"     { return new_symbol(sym.RBRACKET, yytext()); }
"{"     { return new_symbol(sym.LBRACE, yytext()); }
"}"     { return new_symbol(sym.RBRACE, yytext()); }
":"     { return new_symbol(sym.COLON, yytext()); }
"?"     { return new_symbol(sym.QUESTION, yytext()); }

// ============================================================================
// KONSTANTE
// ============================================================================



// Numericka konstanta (celobrojna)
[0-9]+ { 
    return new_symbol(sym.NUMBER, Integer.valueOf(yytext())); 
}

"'"."'" { return new_symbol(sym.CHARACTER, new Character (yytext().charAt(1))); }

// Karakter konstanta
// '[ -~]' { 
    // Preuzimamo karakter izmedju jednostrukih navodnika
//    return new_symbol(sym.CHAR_CONST, Character.valueOf(yytext().charAt(1))); 
//}

// Specijalni escape karakteri u char konstanti
// '\\n' { return new_symbol(sym.CHAR_CONST, Character.valueOf('\n')); }
// '\\t' { return new_symbol(sym.CHAR_CONST, Character.valueOf('\t')); }
// '\\r' { return new_symbol(sym.CHAR_CONST, Character.valueOf('\r')); }
// '\\\\' { return new_symbol(sym.CHAR_CONST, Character.valueOf('\\')); }
// '\\'' { return new_symbol(sym.CHAR_CONST, Character.valueOf('\'')); }

// ============================================================================
// IDENTIFIKATORI
// ============================================================================

// Identifikator: pocinje slovom, zatim slova, cifre ili _
([a-z]|[A-Z])[a-z|A-Z|0-9|_]* { 
    return new_symbol(sym.IDENT, yytext()); 
}

// ============================================================================
// LEKSICKA GRESKA
// ============================================================================

. { 
    System.err.println("Leksicka greska (" + yytext() + ") u liniji " + (yyline + 1) + ", kolona " + (yycolumn + 1)); 
}
