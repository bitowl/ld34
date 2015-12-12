package de.bitowl.ld34;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.XmlReader;

import magory.lib.MaSVG2;

public class SVGLoader extends MaSVG2 {
    World world;

    public SVGLoader(World world) {
        this.world = world;
    }

    @Override
    public void newImage(String name, XmlReader.Element el, float xxx, float yyy, float width, float height, float rr) {

    }

    @Override
    public void newRect(String name, XmlReader.Element el, float x, float y, float width, float height, float rr, String desc) {
        System.out.println("NEW RECT" + name + " " + x+","+y+" "+width+"x"+height);
        System.out.println(desc);

        if (desc.contains("dyxn")) {
            DynamicBox ground = new DynamicBox(width, height);
            ground.setPosition(new Vector2(x, y));
            ground.attachTo(world);
        } else {
            StaticBox ground = new StaticBox(width, height);
            ground.setPosition(new Vector2(x, y));
            ground.attachTo(world);
        }

    }

    @Override
    public void newCircle(String name, XmlReader.Element el, float xxx, float yyy, float width, float height, float rr, String desc) {
        System.out.println("NEW CIRCLE:" + xxx +"," + yyy);
    }

    @Override
    public void newText(String text, XmlReader.Element el, float xxx, float yyy, float width, float height, float rr, Color color) {

    }

    @Override
    public void newPath(Array<Vector2> path, XmlReader.Element el, String title) {
        System.out.println("PAAATH");

        System.out.println(path);

        Vector2[] vertices = new Vector2[path.size];

        for (int i = 0; i < path.size; i++) {
            vertices[i] = path.get(i);
        }

        System.out.println(vertices.length);

        StaticPolygon ground = new StaticPolygon(vertices);
        ground.attachTo(world);
    }


    }
