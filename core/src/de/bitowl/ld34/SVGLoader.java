package de.bitowl.ld34;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.EarClippingTriangulator;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.FloatArray;
import com.badlogic.gdx.utils.ShortArray;
import com.badlogic.gdx.utils.XmlReader;


import java.util.HashMap;

import de.bitowl.ld34.objects.Drop;
import de.bitowl.ld34.objects.Enemy;
import de.bitowl.ld34.objects.Entity;
import de.bitowl.ld34.objects.Exit;
import de.bitowl.ld34.objects.Player;
import de.bitowl.ld34.physics.Box;
import de.bitowl.ld34.physics.Circle;
import de.bitowl.ld34.physics.Polygon;
import magory.lib.MaSVG2;

public class SVGLoader extends MaSVG2 {
    World world;
    Stage stage;
    Player player;

    HashMap<String, Array<Vector2>> paths;
    Array<Enemy> needsPath; // enemies that still need a path

    public SVGLoader(World world, Stage stage, Player player) {
        this.world = world;
        this.stage = stage;
        this.player = player;
        paths = new HashMap<String, Array<Vector2>>();
        needsPath = new Array<Enemy>();
    }

    @Override
    public void newImage(String name, XmlReader.Element el, float xxx, float yyy, float width, float height, float rr) {
        Image image = new Image(Utils.getDrawable(name));
        image.setWidth(width);
        image.setHeight(height);
        image.setOrigin(width/2, height/2);
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

        if (attrs.containsKey("enemy")) {
            Enemy obj = new Enemy(Utils.getDrawable(attrs.get("image")));
            obj.setOrigin(width/2, height/2);
            stage.addActor(obj);
            box.setUserData(obj);

            if (attrs.containsKey("path")) { // I'M A MOVIN' ENEMY
                obj.setPathName(attrs.get("path"));
                needsPath.add(obj);
            }
            if (attrs.containsKey("speed")) {
                obj.setSpeed(Float.parseFloat(attrs.get("speed")));
            }

        } else if (attrs.containsKey("image")) {
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

        // points
        if (attrs.containsKey("player-spawn")) {
            player.getPhysicalObject().setPosition(new Vector2(x, y));
            System.out.println("player: " + x +", "+ y);
         //   System.exit(1);;

        }

        Circle circle = new Circle(r, attrs);
        circle.setPosition(new Vector2(x, y));
        circle.setRotation(rr);
        circle.getFixtureDef().isSensor = true;

        if (attrs.containsKey("drop")) {
            Drop drop = new Drop(r);
            stage.addActor(drop);
            circle.setUserData(drop);
        } else if (attrs.containsKey("exit")) {
            Exit exit = new Exit(player, attrs.containsKey("weight")?Float.parseFloat(attrs.get("weight")):Exit.DEFAULT_WEIGHT);
            stage.addActor(exit);
            circle.setUserData(exit);
        }
        circle.attachTo(world);


    }

    @Override
    public void newText(String text, XmlReader.Element el, float xxx, float yyy, float width, float height, float rr, Color color) {

    }

    @Override
    public void newPath(Array<Vector2> path, XmlReader.Element el, String title) {


        HashMap<String, String> attrs = parseAttributes(el.get("desc", ""));

        if (attrs.containsKey("path")) {
            // this is not a collision object but a path for an enemy
            paths.put(attrs.get("path"), path);
            return;
        }

        System.out.println("PAAATH");

        System.out.println(path);

        Vector2[] vertices = new Vector2[path.size];

        for (int i = 0; i < path.size; i++) {
            vertices[i] = path.get(i).scl(Utils.W2B);
        }
/*// TODO use bether algorithm to split them up into convex polygons

        System.out.println(vertices.length);
        ShortArray list = new EarClippingTriangulator().computeTriangles(GeometryUtils.toFloatArray(path));

        for (int i = 0; i < list.size; i+=3) {
            Vector2[] verti = new Vector2[3];
            verti[0] = vertices[list.get(i)];
            verti[1] = vertices[list.get(i+1)];
            verti[2] = vertices[list.get(i+2)];
            System.out.println(verti[0]);
            System.out.println(verti[1]);
            System.out.println(verti[2]);
            if (verti[0].equals(verti[2]) || verti[0].equals(verti[1]) || verti[1].equals(verti[2])) {
                continue;
            }
            Polygon box = new Polygon(verti, attrs);
            box.attachTo(world);
        }*/

        if (path.size == 0) {
            return;
        }

        path.removeIndex(path.size - 1);
        Array<Array<Vector2>> polygons = Utils.convertShapeOutlineToBox2DFixturesPolygons(path);

        System.out.println("generated " + polygons.size + " polygons");
        for (int i = 0; i < polygons.size; i++) {
            Array<Vector2> polygon = polygons.get(i);
            if (i == polygons.size - 1) {
           //     continue;
            }


            Vector2[] verti = new Vector2[polygon.size];



            for (int j = 0; j < polygon.size; j++) {
                verti[j] = polygon.get(j);
                System.out.println(j+": "+verti[j]);
            }
            Polygon box = new Polygon(verti, attrs);
            box.attachTo(world);
        }


/*
        System.out.println(vertices);
        float[][] parts = GeometryUtils.decompose(GeometryUtils.toFloatArray(new Array<Vector2>(vertices)).toArray());
        System.out.println("split up into " + parts.length + " polygons.");
        for (float[] poly: parts) {
            Polygon box = new Polygon(GeometryUtils.toVector2Array(new FloatArray(poly)).toArray(), attrs);
            box.attachTo(world);
        }*/
        /*

        if (!GeometryUtils.isConvex(vertices)) {
            com.badlogic.gdx.math.Polygon[] polygons = GeometryUtils.decomposeIntoConvex(new com.badlogic.gdx.math.Polygon(Utils.toFloatArray(vertices)));
            System.out.println("split up into " + polygons.length + " polygons.");
            for (com.badlogic.gdx.math.Polygon polygon: polygons) {
                Polygon box = new Polygon(Utils.toVector2Array(polygon.getVertices()), attrs);
                box.attachTo(world);
            }
        } else {
            // simple polygon
            Polygon ground = new Polygon(vertices, attrs);
            ground.attachTo(world);
        }*/

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

    @Override
    public void onFinish() {
        for (Enemy enemy: needsPath) {
            if (!paths.containsKey(enemy.getPathName())) {
                throw new RuntimeException("Path " + enemy.getPathName() + " not defined D:");
            }
            enemy.setPath(paths.get(enemy.getPathName()));
        }
    }
}
