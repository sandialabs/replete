package finio.renderers.map;

import finio.core.FUtil;
import finio.core.NonTerminal;
import finio.core.errors.FMapException;
import finio.core.syntax.FMapSyntax;
import finio.core.syntax.FMapSyntaxLibrary;
import replete.text.StringUtil;
import replete.text.TextReplacementMap;


public class StandardAMapRenderer implements FMapRenderer {


    ////////////
    // FIELDS //
    ////////////

    private static FMapSyntax defaultSyntax = FMapSyntaxLibrary.getSyntax("JSON");
    private FMapSyntax syntax;
    private ParsedPattern parsedPattern;
    private boolean renderSysMeta;


    //////////////////
    // CONSTRUCTORS //
    //////////////////

    public StandardAMapRenderer() {
        this(null);
    }
    public StandardAMapRenderer(FMapSyntax syntax) {
        if(syntax == null) {
            syntax = defaultSyntax;
        }
        this.syntax = syntax;
        parsedPattern = parsePattern();
    }

    public boolean isRenderSysMeta() {
        return renderSysMeta;
    }
    public StandardAMapRenderer setRenderSysMeta(boolean render) {
        renderSysMeta = render;
        return this;
    }


    ////////////////
    // OVERRIDDEN //
    ////////////////

    @Override
    public String renderKey(Object K) {
        StringBuilder buffer = new StringBuilder();
        appendKeyTokens(-1, buffer, K);
        return buffer.toString();
    }

    @Override
    public String renderValue(Object V) {
        return render(null, V);
    }

    @Override
    public String render(Object K, Object V) {
        StringBuilder buffer = new StringBuilder();
        if(K != null) {
            appendKeyTokens(-1, buffer, K);
            appendAssignTokens(-1, buffer, K);
        }
        renderValue(buffer, 0, V);
        return buffer.toString();
    }


    ////////////
    // HELPER //
    ////////////

    private void renderValue(StringBuilder buffer, int level, Object V) {
        if(FUtil.isNonTerminal(V)) {
            appendMapValueTokens(buffer, level, (NonTerminal) V);
        } else {
            appendValueTokens(buffer, level, V);
        }
    }

    private ParsedPattern parsePattern() {
        String pat = syntax.getPattern();
        int po1 = pat.indexOf("$(");
        int po2 = pat.indexOf("${(}");
        int pc1 = pat.indexOf("$)");
        int pc2 = pat.indexOf("${)}");
        if(po1 != -1 && po2 != -1) {
            throw new FMapException("invalid");
        }
        if(pc1 != -1 && pc2 != -1) {
            throw new FMapException("invalid");
        }
        boolean ho = po1 != -1 || po2 != -1;
        boolean hc = pc1 != -1 || pc2 != -1;

        if(ho != hc) {
            throw new FMapException("invalid x");
        }

        if(ho) {
            int ao = Math.max(po1, po2);
            int ac = Math.max(pc1, pc2);
            String pre = pat.substring(0, ao);
            String post;
            if(pc1 != -1) {
                post = pat.substring(pc1 + 2);
            } else {
                post = pat.substring(pc2 + 4);
            }
            String mid;
            if(po1 != -1) {
                mid = pat.substring(po1 + 2, ac);
            } else {
                mid = pat.substring(po2 + 4, ac);
            }
            return new ParsedPattern(pre, mid, post);
        }

        return new ParsedPattern(pat);
    }

    // TODO: RENDERING
    private void appendMapValueTokens(StringBuilder buffer, int level, NonTerminal M) {
//        TextReplacementMap map = new TextReplacementMap();
//        if(M instanceof AList) {
//            map.put("MO", syntax.getListOpen());
//            map.put("MC", syntax.getListClose());
//        } else {
//            map.put("MO", syntax.getMapOpen());
//            map.put("MC", syntax.getMapClose());
//        }
//        String resolvedPre = map.replace(parsedPattern.getPreRendered());
//        buffer.append(resolvedPre);
//        System.out.println("RPRE=[" + resolvedPre + "]");
//        System.out.println(parsedPattern);

        buffer.append(wrap(syntax.getMapOpen(), level));
        int nK = 0;
        for(Object K : M.K()) {
            Object V = M.get(K);
            if(!renderSysMeta && FUtil.isSysMetaKey(K)) {
                continue;
            }
//
//            map.put("PC", nK != M.size() - 1 ? syntax.getPairClose1() : syntax.getPairClose2());
//            map.put("K", K.toString());
//            map.put("V", "?");
//            System.out.println(map);
//            System.out.println(parsedPattern.getMidRendered());
//            buffer.append(map.replace(parsedPattern.getMidRendered()));

            buffer.append(wrap(syntax.getPairOpen(), level + 1));
            appendKeyTokens(level, buffer, K);
            appendAssignTokens(level, buffer, K);
            appendValueTokens(buffer, level, V);
            if(nK != M.size() - 1) {
                buffer.append(wrap(syntax.getPairClose1(), level + 1));
            }
            buffer.append(wrap(syntax.getPairClose2(), level + 1));
            nK++;
        }
        // empty maps?
        if(M.size() != 0) {
           // buffer.append(syntax.get("nl"));
            //buffer.append(StringUtil.spaces(4 * level));
        }
//        String resolvedPost = map.replace(parsedPattern.getPostRendered());
//        buffer.append(resolvedPost);
        buffer.append(wrap(syntax.getMapClose(), level));
    }
    private void appendValueTokens(StringBuilder buffer, int level, Object V) {
        if(V == null) {
            buffer.append("null");  // FUtil.NULL_TEXT not really an option here...?

        } else if(V instanceof String || V instanceof Character) {
            buffer.append(wrap(syntax.getValueOpen(), level + 1));
            String Vappend = V.toString().replaceAll("\\\\", "\\\\\\\\").replaceAll("\"", "\\\\\"");
            buffer.append(Vappend);
            buffer.append(wrap(syntax.getValueClose(), level + 1));

        } else if(V instanceof Number || V instanceof Boolean) {
            buffer.append(V);

        } else if(V instanceof NonTerminal) {
            appendMapValueTokens(buffer, level + 1, (NonTerminal) V);

        } else {  // "Other Java/Native/Platform/Host Object"
            String Vappend = V.toString().replaceAll("\\\\", "\\\\\\\\").replaceAll("\"", "\\\\\"");
            buffer.append(wrap(syntax.getValueOpen(), level + 1));
            buffer.append("NATIVE<");
            buffer.append(StringUtil.toStringObject(V, true));
            buffer.append("> ");
            buffer.append(Vappend);
            buffer.append(wrap(syntax.getValueClose(), level + 1));
        }
    }
    private void appendKeyTokens(int level, StringBuilder buffer, Object K) {
        buffer.append(wrap(syntax.getKeyOpen(), level + 1));
        String Kappend = K.toString().replaceAll("\\\\", "\\\\\\\\").replaceAll("\"", "\\\\\"");
        buffer.append(Kappend);
        buffer.append(wrap(syntax.getKeyClose(), level + 1));
    }
    private void appendAssignTokens(int level, StringBuilder buffer, Object K) {
        buffer.append(wrap(syntax.getAssignOpen(), level + 1));
        buffer.append(wrap(syntax.getAssign(), level + 1));
        buffer.append(wrap(syntax.getAssignClose(), level + 1));
    }

    private String wrap(Object o, int level) {
        if(o == null) {
            return "";
        }
        String chars = o.toString();
        return chars.replaceAll("\\$I", StringUtil.spaces(4 * level));
    }


    /////////////////
    // INNER CLASS //
    /////////////////

    private class ParsedPattern {


        ////////////
        // FIELDS //
        ////////////

        private String pre;
        private String mid;
        private String post;

        private String preRendered;
        private String midRendered;
        private String postRendered;


        //////////////////
        // CONSTRUCTORS //
        //////////////////

        public ParsedPattern(String pre) {
            this(pre, null, null);
        }
        public ParsedPattern(String pre, String mid, String post) {
            this.pre = pre;
            this.mid = mid;
            this.post = post;

            TextReplacementMap map = new TextReplacementMap();
//                map.put("MO", syntax.getMapOpen());
//                map.put("MC", syntax.getMapClose());
            map.put("PO", syntax.getPairOpen());
            map.put("PC", syntax.getPairClose1());
            map.put("KO", syntax.getKeyOpen());
            map.put("KC", syntax.getKeyClose());
            map.put("VO", syntax.getValueOpen());
            map.put("VC", syntax.getValueClose());
            map.put("A", syntax.getAssign());
            map.put("AO", syntax.getAssignOpen());
            map.put("AC", syntax.getAssignClose());
            map.put("I", "\t");
            map.put("N", "\n");

            if(pre != null) {
                preRendered = map.replace(pre);
            }
            if(post != null) {
                postRendered = map.replace(post);
            }
            if(mid != null) {
                map.remove("PC");
                midRendered = map.replace(mid);
            }
        }


        ///////////////
        // ACCESSORS //
        ///////////////

        public String getPre() {
            return pre;
        }
        public String getMid() {
            return mid;
        }
        public String getPost() {
            return post;
        }
        public String getPreRendered() {
            return preRendered;
        }
        public String getMidRendered() {
            return midRendered;
        }
        public String getPostRendered() {
            return postRendered;
        }


        ////////////////
        // OVERRIDDEN //
        ////////////////

        @Override
        public String toString() {
            return "PRE[" + pre + "] + MID[" + mid + "] + POST[" + post + "]";
        }
    }


    //////////
    // MISC //
    //////////

    public static void setDefaultSyntax(FMapSyntax syntax) {
        defaultSyntax = syntax;
    }
}
