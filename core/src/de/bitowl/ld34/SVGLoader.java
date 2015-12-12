package de.bitowl.ld34;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.XmlReader;

import java.util.HashMap;

import magory.lib.MaSVG2;

public class SVGLoader extends MaSVG2 {
    World world;
    Stage stage;

    public SVGLoader(World world, Stage stage) {
        this.world = world;
        this.stage = stage;
    }

    @Override
    public void newImage(String name, XmlReader.Element el, float xxx, float yyy, float width, float height, float rr) {
        System.out.println("image: "+ name);
    }

    @Override
    public void newRect(String name, XmlReader.Element el, float x, float y, float width, float height, float rr, String desc) {
        System.out.println("NEW RECT" + name + " " + x+","+y+" "+width+"x"+height);
        System.out.println(desc);

        if (desc.contains("dyn")) {
            DynamicBox ground = new DynamicBox(width, height);
            ground.setPosition(new Vector2(x, y));
            ground.getFixtureDef().density = 1;

            Entity obj = new Entity(new Texture("crate.png"));
            obj.setOrigin(width/2,height/2);
            stage.addActor(obj);
            ground.setUserData(obj);
            ground.attachTo(world);
        } else {
            StaticBox ground = new StaticBox(width, height);
            ground.setPosition(new Vector2(x, y));
            ground.attachTo(world);
        }

    }

    @Override
    public void newCircle(String name, XmlReader.Element el, float xxx, float yyy, float width, float height, float rr, String desc) {
        System.out.println("NEW CIRCLE:" + xxx + "," + yyy + " XXX " + rr);

        System.out.println(el.getFloat("cy"));


        float r = el.getFloat("r");

        StaticCircle circle = new StaticCircle(r);
        circle.setPosition(new Vector2(xxx + el.getFloat("cx"), yyy - el.getFloat("cy")));
      //  circle.getFixtureDef().isSensor = true;

        HashMap<String, String> attrs = parseAttributes(desc);

        if (attrs.containsKey("drop")) {
            Drop drop = new Drop(r);
            drop.setWidth(r*2);drop.setHeight(r * 2);
            drop.setOrigin(r, r);
            stage.addActor(drop);
            circle.setUserData(drop);
        }

        circle.attachTo(world);


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


    public HashMap<String, String> parseAttributes(String desc) {
        HashMap<String, String> attrs = new HashMap<String, String>();
        String[] lines = desc.split("\n");
        for (String line:lines) {
            String[] parts = line.split("=");
            if (parts.length == 1) {
                attrs.put(parts[0], "");
            } else {
                attrs.put(parts[0], parts[1]);
            }
        }
        return attrs;
    }

}
