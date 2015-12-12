package de.bitowl.ld34;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.XmlReader;

import java.util.HashMap;

import de.bitowl.ld34.objects.Drop;
import de.bitowl.ld34.objects.Entity;
import de.bitowl.ld34.physics.Box;
import de.bitowl.ld34.physics.Circle;
import de.bitowl.ld34.physics.Polygon;
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
        Image image = new Image(Utils.getDrawable(name));
        image.setWidth(width);
        image.setHeight(height);
        image.setPosition(xxx, yyy);
        image.setRotation(rr);
        stage.addActor(image);
        System.out.println("image: " + name);
    }

    @Override
    public void newRect(String name, XmlReader.Element el, float x, float y, float width, float height, float rr, String desc) {
        System.out.println("NEW RECT" + name + " " + x + "," + y + " " + width + "x" + height);
        System.out.println(desc);

        x *= Utils.W2B;
        y *= Utils.W2B;
        width *= Utils.W2B;
        height *= Utils.W2B;

        HashMap<String, String> attrs = parseAttributes(desc);

        Box box = new Box(width, height, attrs);
        box.setPosition(new Vector2(x, y));
        box.setRotation(rr);

        if (attrs.containsKey("image")) {
            Entity obj = new Entity(Utils.getDrawable(attrs.get("image")));
            obj.setOrigin(width/2, height/2);
            stage.addActor(obj);
            box.setUserData(obj);
        }
        box.attachTo(world);
    }

    @Override
    public void newCircle(String name, XmlReader.Element el, float xxx, float yyy, float width, float height, float rr, String desc) {
        System.out.println("NEW CIRCLE:" + xxx + "," + yyy + " XXX " + rr);

        System.out.println(el.getFloat("cy"));

        float x = xxx + el.getFloat("cx");
        float y = yyy - el.getFloat("cy");

        x *= Utils.W2B;
        y *= Utils.W2B;
        float r = el.getFloat("r") * Utils.W2B;

        HashMap<String, String> attrs = parseAttributes(desc);
        Circle circle = new Circle(r, attrs);
        circle.setPosition(new Vector2(x, y));
        circle.setRotation(rr);
        circle.getFixtureDef().isSensor = true;

        if (desc.contains("drop")) {
            Drop drop = new Drop(r * Utils.W2B);
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


        HashMap<String, String> attrs = parseAttributes(el.get("desc", ""));

        System.out.println("PAAATH");

        System.out.println(path);

        Vector2[] vertices = new Vector2[path.size];

        for (int i = 0; i < path.size; i++) {
            vertices[i] = path.get(i);
        }

        System.out.println(vertices.length);

        Polygon ground = new Polygon(vertices, attrs);
        ground.attachTo(world);
    }

    public HashMap<String, String> parseAttributes(String desc) {
        HashMap<String, String> attrs = new HashMap<String, String>();
        String[] lines = desc.split("\n");
        for (String line : lines) {
            String[] parts = line.split("=");
            if (parts.length == 1) {
                attrs.put(parts[0].trim(), "");
            } else {
                attrs.put(parts[0].trim(), parts[1].trim());
            }
        }
        return attrs;
    }

}
