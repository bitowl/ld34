package de.bitowl.ld34;

import java.io.IOException;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.XmlReader.Element;

/**
 * http://www.badlogicgames.com/forum/viewtopic.php?f=17&t=6125&start=10
 */
public abstract class MaSVG2 {
    public float SVGWidth;
    public float SVGHeight;

    public String getTitle(Element el) {
        if (el == null)
            return "";
        Element tit = getChild(el, "title");
        if (tit != null)
            return tit.getText();
        return "";
    }

    public String getParentTitle(Element el) {
        if (el == null)
            return "";
        if (el.getParent() == null)
            return "";
        Element tit = getChild(el.getParent(), "title");
        if (tit != null)
            return tit.getText();
        return "";
    }

    public void load(String name) {
        load(Gdx.files.internal(name));
    }

    public void load(FileHandle file) {
        XmlReader xr = new XmlReader();
        Element lev = new Element("a", null);
        try {
            lev = xr.parse(file);
        } catch (Exception e) {
            Gdx.app.log("test", "Error loading " + file);
        }

        loadElement(lev, 0, 0, 0, 1, 1);
        onFinish();
    }

    // override at will
    public void onFinish() {
    }

    // the rest
    public String getImageName(String trans) {
        String name = "";
        try {
            int en = trans.indexOf(".png");
            int st = trans.lastIndexOf('/');
            name = trans.substring(st + 1, en);
        } catch (Exception e) {
            Gdx.app.log("test", "Error getting filename: " + trans);
        }
        return name;
    }

    public static boolean isDigitOrSign(char a) {
        return Character.isDigit(a) || a == '-';
    }

    public static Array<Vector2> parsePathToArray(String d, float x, float y, float screenheight) {
        Array<Vector2> path = new Array<Vector2>();
        if (!d.equals("")) {
            String commands[] = d.split("\\ ");
            if (commands.length > 0) {
                String lastCommand = "";

                // only m,M,l,L,z,Z commands are supported
                for (int pos = 0; pos < commands.length; pos++) {
                    if (commands[pos].equals("m"))
                        lastCommand = "m";
                    else if (commands[pos].equals("M"))
                        lastCommand = "M";
                    else if (commands[pos].equals("L"))
                        lastCommand = "L";
                    else if (commands[pos].equals("l"))
                        lastCommand = "l";
                    else if (commands[pos].equals("Z") || commands[pos].equals("z")) {
                        // close path
                        if (path.size > 0)
                            path.add(new Vector2(path.get(1)));
                    } else if (commands[pos].length() > 0 &&
                            isDigitOrSign(commands[pos].charAt(0)))// assume coordinates
                    {
                        String coords[] = commands[pos].split(",");
                        if (coords.length > 1) {
                            Vector2 c = new Vector2(getFloat(coords[0]), getFloat(coords[1]));
                            if (path.size > 0 && lastCommand.equals("m") || lastCommand.equals("l")) // relative coords
                            {
                                c.x = path.get(path.size - 1).x + c.x;
                                c.y = path.get(path.size - 1).y + c.y;
                            }
                            path.add(c);
                        }
                    }
                }
                for (int i = 0; i < path.size; i++) {
                    path.get(i).x += x;
                    path.get(i).y += y;
                    path.get(i).y = screenheight - path.get(i).y;
                }
            }
        }
        return path;
    }

    public boolean isScale(String trans) {
        return trans.contains("scale(");
    }

    public boolean isMatrix(String trans) {
        return trans.contains("matrix(");
    }

    public boolean isTranslate(String trans) {
        return trans.contains("translate(");
    }

    public float[] getScaleFloats(String t) {
        return getTwoFloats(t, "scale(");
    }

    public float[] getTranslateFloats(String t) {
        return getTwoFloats(t, "translate(");
    }

    public float[] getMatrixFloats(String t) {
        return getSixFloats(t, "matrix(");
    }

    public void loadElement(Element el, float x, float y, float r, float sx, float sy) {
        if (sx == 0)
            sx = 1;
        if (sy == 0)
            sy = 1;

        String elname = el.getName();
        if (elname.equals("svg")) {
            String w = getAttribute(el, "width", "1280", false);
            String h = getAttribute(el, "height", "800", false);
            w = w.replace("px", "");
            h = h.replace("px", "");
            SVGWidth = getFloat(w);
            SVGHeight = getFloat(h);
        }

        float[] fl = new float[1]; // ugly, I know
        int count = el.getChildCount();

        int sign = 0;
        float r2 = 0;

        // magic for transforms and matrixes, don't touch unless you know what you are doing
        String trans = getAttribute(el, "transform", "", false);

        String t = getTitle(el);

        float elementX = getFloat(el.getAttribute("x", "0"));
        float elementY = getFloat(el.getAttribute("y", "0"));

        if (!trans.equals("")) {
            if (isScale(trans)) {
                fl = getScaleFloats(trans);
                trans = "matrix(" + fl[0] + "," + 0 + "," + 0 + "," + fl[1] + "," + 0 + "," + 0 + ")"; // fake matrix
            } else if (isTranslate(trans)) {
                // just translate, easy
                fl = getTranslateFloats(trans);
                x += fl[0];
                y += fl[1];
            }

            if (isMatrix(trans)) // no else since there might be a matrix from scale(
            {
                fl = getMatrixFloats(trans);

                x += fl[4];
                y += fl[5];
                // new version
                if (fl[1] != 0 || fl[0] != 0)
                    r += (float) (Math.toDegrees(Math.atan2(fl[1], fl[0])));
                else
                    r += 90;
                sign = ((fl[0] * fl[3] - fl[2] * fl[1]) < 0) ? -1 : 1;
                r2 = (float) (Math.toDegrees(Math.atan2(fl[3], fl[2])));

                float oldsx = sx;
                float oldsy = sy;

                if (sign < 0) {
                    if (r2 < 0 && r < 0) {
                        sx *= (fl[0]) / Math.cos(Math.toRadians(r));
                        sy *= (fl[3]) / Math.cos(Math.toRadians(r));

                        if (sy >= 0) {
                            sx = 1 / sx;
                            sy = 1 / sy;
                        }
                    } else {
                        sx *= (fl[0]) / Math.cos(Math.toRadians(r));
                        sy *= (fl[3]) / Math.cos(Math.toRadians(r));

                        if (sx == 0 || sy == 0) // can't be zero for this
                        {
                            sx = (float) (oldsx * (fl[1]) / Math.sin(Math.toRadians(r)));
                            sy = (float) (oldsy * -(fl[2]) / Math.sin(Math.toRadians(r)));
                        }
                    }
                } else if (fl[0] != 0 || fl[3] != 0) {
                    sx *= fl[0] / Math.cos(Math.toRadians(r));
                    sy *= fl[3] / Math.cos(Math.toRadians(r));

                    if (sx == 0 || sy == 0) // can't be zero for this
                    {
                        sx = (float) (oldsx * (fl[1]) / Math.sin(Math.toRadians(r)));
                        sy = (float) (oldsy * -(fl[2]) / Math.sin(Math.toRadians(r)));
                    }
                } else {
                    sx = (float) (oldsx * (fl[1]) / Math.sin(Math.toRadians(r)));
                    sy = (float) (oldsy * -(fl[2]) / Math.sin(Math.toRadians(r)));
                }
            }
        }

        if (count != 0)
            for (int i = 0; i < count; i++) {
                if (fl.length > 2) // matrix
                {
                    if (getTitle(el).equals("testing")) {
                        Gdx.app.log("test", el.getName() + i + ": r:" + r + " x:" + x + " y:" + y + " sx:" + sx + " sy:" + sy);
                        Element e = el.getChild(i);
                    }
                }
                loadElement(el.getChild(i), x, y, r, sx, sy);
            }

        // important magic for width,height and x,y
        float xx = elementX * sx;
        float yy = elementY * sy;
        float width = getFloat(getAttribute(el, "width", "", false)) * sx;
        float height = getFloat(getAttribute(el, "height", "", false)) * sy;
        float widthSign = 1;
        float heightSign = 1;
        if (width < 0)
            widthSign = -1; //TODO sprawdzić czy na pewno tak!

        float yyy = SVGHeight - (y + yy) - height;
        float xxx = x + xx;
        float rr = r;
        float originX = width / 2;
        float originY = height / 2;

        if (fl.length > 2) // matrix
        {
            xx += originX; // it assumes you set originX and originY as width/2 and height/2
            yy += originY;

            if (sign < 0) {
                xx = getFloat(getAttribute(el, "x", "0", false)) * (1 / sx);
                yy = getFloat(getAttribute(el, "y", "0", false)) * (1 / sy);
                xx += originX;
                yy += originY;
            }

            float additiveX = (float) (xx * Math.cos(Math.toRadians(rr)) - yy * Math.sin(Math.toRadians(rr))) - originX;
            float additiveY = (float) (xx * Math.sin(Math.toRadians(rr)) + yy * Math.cos(Math.toRadians(rr))) + sign * originY;
            xxx = x + widthSign * additiveX;
            yyy = SVGHeight - y - widthSign * additiveY;

            if (getTitle(el).equals("testingZ"))
                Gdx.app.log("test", "IS xxx:" + xxx + " yyy:" + yyy + " rr:" + rr + " xx:" + sx + " yy:" + sy + "width:" + width + "height:" + height);

            rr = -rr;
            if (widthSign < 0)
                rr = rr + 180;
        } else if (r != 0) // was rotation
        {
            xx += originX; // it assumes you set originX and originY as width/2 and height/2
            yy += originY;
            xxx = (float) (x + xx * Math.cos(Math.toRadians(rr))
                    - yy * Math.sin(Math.toRadians(rr))) - originX;
            yyy = SVGHeight - (float) (y + xx * Math.sin(Math.toRadians(rr))
                    + yy * Math.cos(Math.toRadians(rr))) - originY;

            rr = -rr;
        }

        if (elname.equals("path")) // path
        {
            String title = getTitle(el);
            String d = el.getAttribute("d", "");
            newPath(parsePathToArray(d, x, y, SVGHeight), el, title);
        } else if (isText(elname)) // text
        {
            Element e = getChild(el, "tspan");
            if (e == null)
                return;

            String text;
            if (e.getText() == null)
                text = "";
            else
                text = e.getText().trim();

            // font-size as height! - width not set unfortunately
            // example: font-size:44.03109741px;
            String style = getAttribute(el, "style", "", false);
            String styles[] = style.split("\\;");
            Color color = new Color(1, 1, 1, 1);
            if (styles != null && styles.length > 0) {
                for (int i = 0; i < styles.length; i++) {
                    if (isStyle(styles[i], "font-size")) {
                        String stylesdata[] = styles[i].split("\\:");
                        stylesdata[1] = stylesdata[1].replace("px", "");
                        stylesdata[1] = stylesdata[1].replace(";", "");
                        height = getFloat(stylesdata[1].trim());
                    } else if (isStyle(styles[i], "fill")) {
                        //fill:#effffa
                        String stylesdata[] = styles[i].split("\\:");
                        stylesdata[1] = stylesdata[1].replace("#", "");
                        stylesdata[1] = stylesdata[1].replace(";", "");
                        color = getColorFromString(stylesdata[1].trim());
                    }
                }
            }
            newText(text, el, xxx, yyy, width, height, rr, color);
        } else if (isImage(elname))  // obraz
        {
            String name = getImageName(getAttribute(el, "xlink:href", "", false));
            newImage(name, el, xxx, yyy, width, height, rr);
        } else if (isRect(elname)) // obraz
        {
            Element title = getChild(el, "title");
            Element desc = getChild(el, "desc");
            newRect(title != null?title.getText():"", el, xxx, yyy, width, height, rr, desc!=null?desc.getText():"");
        } else if (isCircle(elname)) {
            // TODO change calculation of position
            Element title = getChild(el, "title");
            Element desc = getChild(el, "desc");
            newCircle(title != null ? title.getText() : "", el, xxx, yyy, width, height, rr, desc != null ? desc.getText() : "");
        }
    }

    public boolean isRect(String elname) {
        return elname.equals("rect");
    }

    public boolean isImage(String elname) {
        return elname.equals("image");
    }

    public boolean isText(String elname) {
        return elname.equals("text");
    }

    public boolean isCircle(String elname) {
        return elname.equals("circle");
    }

    public boolean isStyle(String s, String style) {
        if (s.startsWith(style + ":"))
            return true;
        return false;
    }

    public Element getChild(Element el, String name) {
        return el.getChildByName(name);
    }

    public String getAttribute(Element el, String name, String defaultValue, boolean parent) {
        return el.getAttribute(name, defaultValue);
    }

    public String getAttributeValue(Element el, String attribute, String name, String defaultValue) {
        // style="image-rendering:optimizeQuality;opacity:0.613"
        String att = getAttribute(el, "style", "", false);
        String value = "";
        try {
            int st = att.indexOf(name + ":");
            if (st == -1)
                return value;

            int en = att.indexOf(';', st);
            if (en == -1)
                en = att.indexOf('"', st);
            if (en == -1)
                return value;

            value = att.substring(st + 1, en);
        } catch (Exception e) {
            Gdx.app.log("test", "Error getting value: " + attribute + "=" + name + ":VALUE;");
        }
        return value;
    }

    // gets opacity from image element
    public float getOpacity(Element el) {
        float opacity = 1;
        String op = getAttributeValue(el, "style", "opacity", "1");
        if (!op.equals("")) {
            opacity = getFloat(op, 1);
        }
        return opacity;
    }

    public String getColor(String s) {
        String f = "f:";
        int start = s.indexOf(f); // compressed
        if (start == -1) {
            f = "fill:";
            start = s.indexOf(f); // not compressed
        }
        int end = s.indexOf(";", start);
        if (end > start)
            return s.substring(start + f.length(), end);
        return "";
    }

    public static int getHexValue(char c) {
        if (c >= 'a')
            return c - 'a' + 10;
        else
            return c - '0';
    }

    public static Color getColorFromString(String c) {
        if (c.length() > 5) {
            //Gdx.app.log("test", c);
            float r = getHexValue(c.charAt(0)) * 16 + getHexValue(c.charAt(1));
            float g = getHexValue(c.charAt(2)) * 16 + getHexValue(c.charAt(3));
            float b = getHexValue(c.charAt(4)) * 16 + getHexValue(c.charAt(5));
            r /= 256f;
            g /= 256f;
            b /= 256f;
            //Gdx.app.log("test", "r"+r+"g"+g+"b"+b);
            return new Color(r, g, b, 1);
        } else
            return new Color(1, 1, 1, 1);
    }
    // actions to override

    abstract public void newImage(String name, Element el, float x, float y,
                                  float width, float height, float r);

    abstract public void newRect(String name, Element el, float x, float y,
                                 float width, float height, float r, String desc);

    abstract public void newCircle(String name, Element el, float x, float y,
                                 float width, float height, float r, String desc);

    abstract public void newText(String text, Element el, float x, float y,
                                 float width, float height, float r, Color color);

    abstract public void newPath(Array<Vector2> path, Element el, String title);

    // statics

    public static float[] getSixFloats(String trans, String search) {
        int st = trans.indexOf(search);
        float[] fl = new float[6];
        float xx = 0;
        float yy = 0;

        float zz = 0;
        float x2 = 0;
        float y2 = 0;
        float z2 = 0;

        if (st != -1) // jest translate
        {
            int comma = trans.indexOf(",", st);
            int bracket = trans.indexOf(")", st);
            int comma2 = trans.indexOf(",", comma + 1);
            try {

                xx = new Float(trans.substring(st + search.length(), comma));
                yy = new Float(trans.substring(comma + 1, comma2));
                comma = comma2;
                comma2 = trans.indexOf(",", comma + 1);
                zz = new Float(trans.substring(comma + 1, comma2));

                comma = comma2;
                comma2 = trans.indexOf(",", comma + 1);
                x2 = new Float(trans.substring(comma + 1, comma2));

                comma = comma2;
                comma2 = trans.indexOf(",", comma + 1);
                y2 = new Float(trans.substring(comma + 1, comma2));

                comma = comma2;
                z2 = new Float(trans.substring(comma + 1, bracket));

            } catch (Exception e) {

            }
        }
        fl[0] = xx;
        fl[1] = yy;
        fl[2] = zz;
        fl[3] = x2;
        fl[4] = y2;
        fl[5] = z2;
        return fl;
    }

    public static float[] getTwoFloats(String trans, String search) {
        int st = trans.indexOf(search);
        float[] fl = new float[2];
        float xx = 0;
        float yy = 0;
        if (st != -1) // jest translate
        {
            int comma = trans.indexOf(",", st);
            int bracket = trans.indexOf(")", st);
            try {

                xx = new Float(trans.substring(st + search.length(), comma));
                yy = new Float(trans.substring(comma + 1, bracket));
            } catch (Exception e) {

            }
        }
        fl[0] = xx;
        fl[1] = yy;
        return fl;
    }

    public static int getInt(String t, String beg) {
        return getInt(t.substring(beg.length()));
    }

    public static int getInt(String trans) {
        try {
            return new Integer(trans);
        } catch (Exception e) {
        }
        return 0;
    }

    public static float getFloat(String t, String beg) {
        return getFloat(t.substring(beg.length()));
    }

    public static float getFloat(String trans) {
        try {
            return new Float(trans);
        } catch (Exception e) {
        }
        return 0;
    }

    public static float getFloat(String trans, float defaultValue) {
        try {
            return new Float(trans);
        } catch (Exception e) {
        }
        return defaultValue;
    }
}