package de.bitowl.ld34.objects;

import de.bitowl.ld34.Utils;

public class Drop extends Entity {
    private float size;
    public Drop(float size) {
        super(Utils.getDrawable("drop"));
        this.size = size;
    }

    @Override
    public void collide(Entity userData) {
        if (userData instanceof Player) {
            setToBeRemoved();
        }
    }

    public float getSize() {
        return size;
    }
}
