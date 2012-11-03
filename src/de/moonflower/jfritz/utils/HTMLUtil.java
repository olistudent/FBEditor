package de.moonflower.jfritz.utils;
import java.util.HashMap;

/**
 * Strips HTML entities such as &quot; from a file, replacing them by their
 * Unicode equivalents. Methods can be used on text strings as well. Does not
 * strip Tags, just Entities. No longer requires entitiestochar.ser in the jar!
 * 
 * @author Roedy Green
 * @version 1.5
 * borrowed from: http://mindprod.com/zips/java/entities15.zip
 */

public class HTMLUtil {

    /**
     * allows lookup by entity name, to get the corresponding char.
     */
    private static HashMap<String, Character> entityToChar;

    /**
     * Longest an entity can be {@value #LONGEST_ENTITY}, at least in our
     * tables, including the lead & and trail ;
     */
    public static final int LONGEST_ENTITY = 10; /* &thetasym; */

    /**
     * The shortest an entity can be {@value #SHORTEST_ENTITY}, at least in our
     * tables, including the lead & and trailing ;
     */
    public static final int SHORTEST_ENTITY = 4; /* &#1; &lt; */

    static
        {
        // build HashMap to look up entity name to get corresponding Unicode
        // char number. Following code generated by Entities.
        String[] entityKeys = {
            "quot" /* 34 */,
            "amp" /* 38 */,
            "lt" /* 60 */,
            "gt" /* 62 */,
            "nbsp" /* 160 */,
            "iexcl" /* 161 */,
            "cent" /* 162 */,
            "pound" /* 163 */,
            "curren" /* 164 */,
            "yen" /* 165 */,
            "brvbar" /* 166 */,
            "sect" /* 167 */,
            "uml" /* 168 */,
            "copy" /* 169 */,
            "ordf" /* 170 */,
            "laquo" /* 171 */,
            "not" /* 172 */,
            "shy" /* 173 */,
            "reg" /* 174 */,
            "macr" /* 175 */,
            "deg" /* 176 */,
            "plusmn" /* 177 */,
            "sup2" /* 178 */,
            "sup3" /* 179 */,
            "acute" /* 180 */,
            "micro" /* 181 */,
            "para" /* 182 */,
            "middot" /* 183 */,
            "cedil" /* 184 */,
            "sup1" /* 185 */,
            "ordm" /* 186 */,
            "raquo" /* 187 */,
            "frac14" /* 188 */,
            "frac12" /* 189 */,
            "frac34" /* 190 */,
            "iquest" /* 191 */,
            "Agrave" /* 192 */,
            "Aacute" /* 193 */,
            "Acirc" /* 194 */,
            "Atilde" /* 195 */,
            "Auml" /* 196 */,
            "Aring" /* 197 */,
            "AElig" /* 198 */,
            "Ccedil" /* 199 */,
            "Egrave" /* 200 */,
            "Eacute" /* 201 */,
            "Ecirc" /* 202 */,
            "Euml" /* 203 */,
            "Igrave" /* 204 */,
            "Iacute" /* 205 */,
            "Icirc" /* 206 */,
            "Iuml" /* 207 */,
            "ETH" /* 208 */,
            "Ntilde" /* 209 */,
            "Ograve" /* 210 */,
            "Oacute" /* 211 */,
            "Ocirc" /* 212 */,
            "Otilde" /* 213 */,
            "Ouml" /* 214 */,
            "times" /* 215 */,
            "Oslash" /* 216 */,
            "Ugrave" /* 217 */,
            "Uacute" /* 218 */,
            "Ucirc" /* 219 */,
            "Uuml" /* 220 */,
            "Yacute" /* 221 */,
            "THORN" /* 222 */,
            "szlig" /* 223 */,
            "agrave" /* 224 */,
            "aacute" /* 225 */,
            "acirc" /* 226 */,
            "atilde" /* 227 */,
            "auml" /* 228 */,
            "aring" /* 229 */,
            "aelig" /* 230 */,
            "ccedil" /* 231 */,
            "egrave" /* 232 */,
            "eacute" /* 233 */,
            "ecirc" /* 234 */,
            "euml" /* 235 */,
            "igrave" /* 236 */,
            "iacute" /* 237 */,
            "icirc" /* 238 */,
            "iuml" /* 239 */,
            "eth" /* 240 */,
            "ntilde" /* 241 */,
            "ograve" /* 242 */,
            "oacute" /* 243 */,
            "ocirc" /* 244 */,
            "otilde" /* 245 */,
            "ouml" /* 246 */,
            "divide" /* 247 */,
            "oslash" /* 248 */,
            "ugrave" /* 249 */,
            "uacute" /* 250 */,
            "ucirc" /* 251 */,
            "uuml" /* 252 */,
            "yacute" /* 253 */,
            "thorn" /* 254 */,
            "yuml" /* 255 */,
            "OElig" /* 338 */,
            "oelig" /* 339 */,
            "Scaron" /* 352 */,
            "scaron" /* 353 */,
            "Yuml" /* 376 */,
            "fnof" /* 402 */,
            "circ" /* 710 */,
            "tilde" /* 732 */,
            "Alpha" /* 913 */,
            "Beta" /* 914 */,
            "Gamma" /* 915 */,
            "Delta" /* 916 */,
            "Epsilon" /* 917 */,
            "Zeta" /* 918 */,
            "Eta" /* 919 */,
            "Theta" /* 920 */,
            "Iota" /* 921 */,
            "Kappa" /* 922 */,
            "Lambda" /* 923 */,
            "Mu" /* 924 */,
            "Nu" /* 925 */,
            "Xi" /* 926 */,
            "Omicron" /* 927 */,
            "Pi" /* 928 */,
            "Rho" /* 929 */,
            "Sigma" /* 931 */,
            "Tau" /* 932 */,
            "Upsilon" /* 933 */,
            "Phi" /* 934 */,
            "Chi" /* 935 */,
            "Psi" /* 936 */,
            "Omega" /* 937 */,
            "alpha" /* 945 */,
            "beta" /* 946 */,
            "gamma" /* 947 */,
            "delta" /* 948 */,
            "epsilon" /* 949 */,
            "zeta" /* 950 */,
            "eta" /* 951 */,
            "theta" /* 952 */,
            "iota" /* 953 */,
            "kappa" /* 954 */,
            "lambda" /* 955 */,
            "mu" /* 956 */,
            "nu" /* 957 */,
            "xi" /* 958 */,
            "omicron" /* 959 */,
            "pi" /* 960 */,
            "rho" /* 961 */,
            "sigmaf" /* 962 */,
            "sigma" /* 963 */,
            "tau" /* 964 */,
            "upsilon" /* 965 */,
            "phi" /* 966 */,
            "chi" /* 967 */,
            "psi" /* 968 */,
            "omega" /* 969 */,
            "thetasym" /* 977 */,
            "upsih" /* 978 */,
            "piv" /* 982 */,
            "ensp" /* 8194 */,
            "emsp" /* 8195 */,
            "thinsp" /* 8201 */,
            "zwnj" /* 8204 */,
            "zwj" /* 8205 */,
            "lrm" /* 8206 */,
            "rlm" /* 8207 */,
            "ndash" /* 8211 */,
            "mdash" /* 8212 */,
            "lsquo" /* 8216 */,
            "rsquo" /* 8217 */,
            "sbquo" /* 8218 */,
            "ldquo" /* 8220 */,
            "rdquo" /* 8221 */,
            "bdquo" /* 8222 */,
            "dagger" /* 8224 */,
            "Dagger" /* 8225 */,
            "bull" /* 8226 */,
            "hellip" /* 8230 */,
            "permil" /* 8240 */,
            "prime" /* 8242 */,
            "Prime" /* 8243 */,
            "lsaquo" /* 8249 */,
            "rsaquo" /* 8250 */,
            "oline" /* 8254 */,
            "frasl" /* 8260 */,
            "euro" /* 8364 */,
            "image" /* 8465 */,
            "weierp" /* 8472 */,
            "real" /* 8476 */,
            "trade" /* 8482 */,
            "alefsym" /* 8501 */,
            "larr" /* 8592 */,
            "uarr" /* 8593 */,
            "rarr" /* 8594 */,
            "darr" /* 8595 */,
            "harr" /* 8596 */,
            "crarr" /* 8629 */,
            "lArr" /* 8656 */,
            "uArr" /* 8657 */,
            "rArr" /* 8658 */,
            "dArr" /* 8659 */,
            "hArr" /* 8660 */,
            "forall" /* 8704 */,
            "part" /* 8706 */,
            "exist" /* 8707 */,
            "empty" /* 8709 */,
            "nabla" /* 8711 */,
            "isin" /* 8712 */,
            "notin" /* 8713 */,
            "ni" /* 8715 */,
            "prod" /* 8719 */,
            "sum" /* 8721 */,
            "minus" /* 8722 */,
            "lowast" /* 8727 */,
            "radic" /* 8730 */,
            "prop" /* 8733 */,
            "infin" /* 8734 */,
            "ang" /* 8736 */,
            "and" /* 8743 */,
            "or" /* 8744 */,
            "cap" /* 8745 */,
            "cup" /* 8746 */,
            "int" /* 8747 */,
            "there4" /* 8756 */,
            "sim" /* 8764 */,
            "cong" /* 8773 */,
            "asymp" /* 8776 */,
            "ne" /* 8800 */,
            "equiv" /* 8801 */,
            "le" /* 8804 */,
            "ge" /* 8805 */,
            "sub" /* 8834 */,
            "sup" /* 8835 */,
            "nsub" /* 8836 */,
            "sube" /* 8838 */,
            "supe" /* 8839 */,
            "oplus" /* 8853 */,
            "otimes" /* 8855 */,
            "perp" /* 8869 */,
            "sdot" /* 8901 */,
            "lceil" /* 8968 */,
            "rceil" /* 8969 */,
            "lfloor" /* 8970 */,
            "rfloor" /* 8971 */,
            "lang" /* 9001 */,
            "rang" /* 9002 */,
            "loz" /* 9674 */,
            "spades" /* 9824 */,
            "clubs" /* 9827 */,
            "hearts" /* 9829 */,
            "diams" /* 9830 */,
        };
        char[] entityValues = {
            34 /* &quot; */,
            38 /* &amp; */,
            60 /* &lt; */,
            62 /* &gt; */,
            160 /* &nbsp; */,
            161 /* &iexcl; */,
            162 /* &cent; */,
            163 /* &pound; */,
            164 /* &curren; */,
            165 /* &yen; */,
            166 /* &brvbar; */,
            167 /* &sect; */,
            168 /* &uml; */,
            169 /* &copy; */,
            170 /* &ordf; */,
            171 /* &laquo; */,
            172 /* &not; */,
            173 /* &shy; */,
            174 /* &reg; */,
            175 /* &macr; */,
            176 /* &deg; */,
            177 /* &plusmn; */,
            178 /* &sup2; */,
            179 /* &sup3; */,
            180 /* &acute; */,
            181 /* &micro; */,
            182 /* &para; */,
            183 /* &middot; */,
            184 /* &cedil; */,
            185 /* &sup1; */,
            186 /* &ordm; */,
            187 /* &raquo; */,
            188 /* &frac14; */,
            189 /* &frac12; */,
            190 /* &frac34; */,
            191 /* &iquest; */,
            192 /* &Agrave; */,
            193 /* &Aacute; */,
            194 /* &Acirc; */,
            195 /* &Atilde; */,
            196 /* &Auml; */,
            197 /* &Aring; */,
            198 /* &AElig; */,
            199 /* &Ccedil; */,
            200 /* &Egrave; */,
            201 /* &Eacute; */,
            202 /* &Ecirc; */,
            203 /* &Euml; */,
            204 /* &Igrave; */,
            205 /* &Iacute; */,
            206 /* &Icirc; */,
            207 /* &Iuml; */,
            208 /* &ETH; */,
            209 /* &Ntilde; */,
            210 /* &Ograve; */,
            211 /* &Oacute; */,
            212 /* &Ocirc; */,
            213 /* &Otilde; */,
            214 /* &Ouml; */,
            215 /* &times; */,
            216 /* &Oslash; */,
            217 /* &Ugrave; */,
            218 /* &Uacute; */,
            219 /* &Ucirc; */,
            220 /* &Uuml; */,
            221 /* &Yacute; */,
            222 /* &THORN; */,
            223 /* &szlig; */,
            224 /* &agrave; */,
            225 /* &aacute; */,
            226 /* &acirc; */,
            227 /* &atilde; */,
            228 /* &auml; */,
            229 /* &aring; */,
            230 /* &aelig; */,
            231 /* &ccedil; */,
            232 /* &egrave; */,
            233 /* &eacute; */,
            234 /* &ecirc; */,
            235 /* &euml; */,
            236 /* &igrave; */,
            237 /* &iacute; */,
            238 /* &icirc; */,
            239 /* &iuml; */,
            240 /* &eth; */,
            241 /* &ntilde; */,
            242 /* &ograve; */,
            243 /* &oacute; */,
            244 /* &ocirc; */,
            245 /* &otilde; */,
            246 /* &ouml; */,
            247 /* &divide; */,
            248 /* &oslash; */,
            249 /* &ugrave; */,
            250 /* &uacute; */,
            251 /* &ucirc; */,
            252 /* &uuml; */,
            253 /* &yacute; */,
            254 /* &thorn; */,
            255 /* &yuml; */,
            338 /* &OElig; */,
            339 /* &oelig; */,
            352 /* &Scaron; */,
            353 /* &scaron; */,
            376 /* &Yuml; */,
            402 /* &fnof; */,
            710 /* &circ; */,
            732 /* &tilde; */,
            913 /* &Alpha; */,
            914 /* &Beta; */,
            915 /* &Gamma; */,
            916 /* &Delta; */,
            917 /* &Epsilon; */,
            918 /* &Zeta; */,
            919 /* &Eta; */,
            920 /* &Theta; */,
            921 /* &Iota; */,
            922 /* &Kappa; */,
            923 /* &Lambda; */,
            924 /* &Mu; */,
            925 /* &Nu; */,
            926 /* &Xi; */,
            927 /* &Omicron; */,
            928 /* &Pi; */,
            929 /* &Rho; */,
            931 /* &Sigma; */,
            932 /* &Tau; */,
            933 /* &Upsilon; */,
            934 /* &Phi; */,
            935 /* &Chi; */,
            936 /* &Psi; */,
            937 /* &Omega; */,
            945 /* &alpha; */,
            946 /* &beta; */,
            947 /* &gamma; */,
            948 /* &delta; */,
            949 /* &epsilon; */,
            950 /* &zeta; */,
            951 /* &eta; */,
            952 /* &theta; */,
            953 /* &iota; */,
            954 /* &kappa; */,
            955 /* &lambda; */,
            956 /* &mu; */,
            957 /* &nu; */,
            958 /* &xi; */,
            959 /* &omicron; */,
            960 /* &pi; */,
            961 /* &rho; */,
            962 /* &sigmaf; */,
            963 /* &sigma; */,
            964 /* &tau; */,
            965 /* &upsilon; */,
            966 /* &phi; */,
            967 /* &chi; */,
            968 /* &psi; */,
            969 /* &omega; */,
            977 /* &thetasym; */,
            978 /* &upsih; */,
            982 /* &piv; */,
            8194 /* &ensp; */,
            8195 /* &emsp; */,
            8201 /* &thinsp; */,
            8204 /* &zwnj; */,
            8205 /* &zwj; */,
            8206 /* &lrm; */,
            8207 /* &rlm; */,
            8211 /* &ndash; */,
            8212 /* &mdash; */,
            8216 /* &lsquo; */,
            8217 /* &rsquo; */,
            8218 /* &sbquo; */,
            8220 /* &ldquo; */,
            8221 /* &rdquo; */,
            8222 /* &bdquo; */,
            8224 /* &dagger; */,
            8225 /* &Dagger; */,
            8226 /* &bull; */,
            8230 /* &hellip; */,
            8240 /* &permil; */,
            8242 /* &prime; */,
            8243 /* &Prime; */,
            8249 /* &lsaquo; */,
            8250 /* &rsaquo; */,
            8254 /* &oline; */,
            8260 /* &frasl; */,
            8364 /* &euro; */,
            8465 /* &image; */,
            8472 /* &weierp; */,
            8476 /* &real; */,
            8482 /* &trade; */,
            8501 /* &alefsym; */,
            8592 /* &larr; */,
            8593 /* &uarr; */,
            8594 /* &rarr; */,
            8595 /* &darr; */,
            8596 /* &harr; */,
            8629 /* &crarr; */,
            8656 /* &lArr; */,
            8657 /* &uArr; */,
            8658 /* &rArr; */,
            8659 /* &dArr; */,
            8660 /* &hArr; */,
            8704 /* &forall; */,
            8706 /* &part; */,
            8707 /* &exist; */,
            8709 /* &empty; */,
            8711 /* &nabla; */,
            8712 /* &isin; */,
            8713 /* &notin; */,
            8715 /* &ni; */,
            8719 /* &prod; */,
            8721 /* &sum; */,
            8722 /* &minus; */,
            8727 /* &lowast; */,
            8730 /* &radic; */,
            8733 /* &prop; */,
            8734 /* &infin; */,
            8736 /* &ang; */,
            8743 /* &and; */,
            8744 /* &or; */,
            8745 /* &cap; */,
            8746 /* &cup; */,
            8747 /* &int; */,
            8756 /* &there4; */,
            8764 /* &sim; */,
            8773 /* &cong; */,
            8776 /* &asymp; */,
            8800 /* &ne; */,
            8801 /* &equiv; */,
            8804 /* &le; */,
            8805 /* &ge; */,
            8834 /* &sub; */,
            8835 /* &sup; */,
            8836 /* &nsub; */,
            8838 /* &sube; */,
            8839 /* &supe; */,
            8853 /* &oplus; */,
            8855 /* &otimes; */,
            8869 /* &perp; */,
            8901 /* &sdot; */,
            8968 /* &lceil; */,
            8969 /* &rceil; */,
            8970 /* &lfloor; */,
            8971 /* &rfloor; */,
            9001 /* &lang; */,
            9002 /* &rang; */,
            9674 /* &loz; */,
            9824 /* &spades; */,
            9827 /* &clubs; */,
            9829 /* &hearts; */,
            9830 /* &diams; */,

        };
        entityToChar = new HashMap<String, Character>( 511 );
        for ( int i = 0; i < entityKeys.length; i++ )
            {
            entityToChar.put( entityKeys[ i ],
                new Character( entityValues[ i ] ) );
            }
        }

    /**
     * convert an entity to a single char
     * 
     * @param entity
     *        String entity to convert convert. must have lead & and trail ;
     *        stripped; may be a x#123 or #123 style entity. Works faster if
     *        entity in lower case.
     * @return equivalent character. 0 if not recognised.
     */
    public static char entityToChar ( String entity )
        {
        Character code = entityToChar.get( entity );
        if ( code != null ) { return code.charValue(); }
        code = entityToChar.get( entity.toLowerCase() );
        if ( code != null ) { return code.charValue(); }
        // check at least have &#1;
        if ( entity.length() < 2 ) return 0;
        try
            {

            switch ( entity.charAt( 0 ) )
            {
            case 'x' :
            case 'X' :
                // handle hex entities
                if ( entity.charAt( 1 ) != '#' ) return 0;
                // ensure at least have &x#1;
                if ( entity.length() < 3 ) return 0;
                // had &x#123D;
                return (char)Integer.parseInt( entity.substring( 2 ), /* hex */
                16 );

            case '#' :
                // handle decimal entities
                // had &#123;
                return (char)Integer.parseInt( entity.substring( 1 ) );

            default :
                return 0;
            }
            }
        catch ( NumberFormatException e )
            {
            return 0;
            }
        } // end entityToChar

    /**
     * Checks a number of gauntlet conditions to ensure this is a valid entity.
     * Converts Entity to corresponding char.
     * 
     * @param possEntity
     *        string that may hold an entity. Lead & must be stripped, but may
     *        contain text past the ;
     * @return corresponding unicode character, or 0 if the entity is invalid.
     */
    public static char possEntityToChar ( String possEntity )
        {
        if ( possEntity.length() < SHORTEST_ENTITY - 1 ) return 0;

        // find the trailing ;
        int whereSemi = possEntity
            .indexOf( ';', SHORTEST_ENTITY - 2 /* where start looking */);
        if ( whereSemi < SHORTEST_ENTITY - 2 ) return 0;

        // we found a potential entity, at least it has &xxxx;
        // lead & already stripped, now strip trailing ;
        // and look it up in a table.
        // Will return 0 for an invalid entity.
        return entityToChar( possEntity.substring( 0, whereSemi ) );
        } // end possEntityToChar

    /**
     * Converts HTML to text converting entities such as &quot; back to " and
     * &lt; back to &lt; Ordinary text passes unchanged.
     * 
     * @param text
     *        raw text to be processed. Must not be null.
     * @return translated text. It also handles HTML 4.0 entities such as
     *         &hearts; &#123; and &x#123; &nbsp; -> 160. null input returns
     *         null.
     */
    public static String stripEntities ( String text )
        {
        if ( text == null ) { return null; }
        if ( text.indexOf( '&' ) < 0 )
            {
            // are no entities, nothing to do
            return text;
            }
        int originalTextLength = text.length();
        StringBuffer sb = new StringBuffer( originalTextLength );
        for ( int i = 0; i < originalTextLength; i++ )
            {
            int whereAmp = text.indexOf( '&', i );
            if ( whereAmp < 0 )
                {
                // no more &s, we are done
                // append all remaining text
                sb.append( text.substring( i ) );
                break;
                }
            else
                {
                // append all text to left of next &
                sb.append( text.substring( i, whereAmp ) );
                // avoid reprocessing those chars
                i = whereAmp;
                // text.charAt(i) is an &
                // possEntity has lead & stripped.
                String possEntity = text.substring( i + 1, Math.min( i
                    + LONGEST_ENTITY, text.length() ) );
                char t = possEntityToChar( possEntity );
                if ( t != 0 )
                    {
                    // was a good entity, keep its equivalent char.
                    sb.append( t );
                    // avoid reprocessing chars forming the entity
                    int whereSemi = possEntity.indexOf( ";",
                        SHORTEST_ENTITY - 2 );
                    i += whereSemi + 1;
                    }
                else
                    {
                    // treat & just as ordinary character
                    sb.append( '&' );
                    }
                } // end else
            } // end for
        // if result is not shorter, we did not do anything. Saves RAM.
        return ( sb.length() == originalTextLength ) ? text : stripNbsp(sb.toString());
        } // end stripEntities

    /**
     * Removes tags from HTML leaving just the raw text. Leaves entities as is,
     * e.g. does not convert &amp; back to &. similar to code in Quoter.
     * 
     * @param html
     *        input HTML
     * @return raw text, with whitespaces collapsed to a single space.
     */
    public static String stripHTMLTags ( String html )
        {
        int numChars = html.length();
        StringBuffer result = new StringBuffer( numChars );
        boolean inside = false;
        boolean lastCharSpace = false;
        for ( int i = 0; i < numChars; i++ )
            {
            char c = html.charAt( i );
            if ( c == '<' )
                {
                inside = true;
                }
            else if ( c == '>' )
                {
                inside = false;
                }
            else if ( Character.isWhitespace( c ) )
                {
                if ( !inside && !lastCharSpace )
                    {
                    result.append( ' ' );
                    lastCharSpace = true;
                    }
                }
            else if ( inside )
                {
                // ignore while inside tag
                }
            else
                {
                result.append( c );
                lastCharSpace = false;
                }
            }
        return result.toString();
        }

    /**
     * converts all 160-style spaces (result of stripEntities on &nbsp;) to
     * ordinary space.
     * 
     * @param text
     *        Text to convert
     * @return Text with 160-style spaces converted to ordinary spaces
     */
    public static String stripNbsp ( String text )
        {
        return text.replace( (char)160, ' ' );
        }

} // end StripEntities
