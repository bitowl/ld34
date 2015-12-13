package de.bitowl.ld34;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;

import de.bitowl.ld34.objects.Entity;
import de.bitowl.ld34.objects.Player;

class ExtremeContactListener implements ContactListener {
    @Override
    public void beginContact(Contact contact) {

        if (contact.getFixtureA().getBody().getUserData() instanceof Player) {
            ((Player) contact.getFixtureA().getBody().getUserData()).simpleContact(contact);
        }
        if (contact.getFixtureB().getBody().getUserData() instanceof Player) {
            ((Player) contact.getFixtureB().getBody().getUserData()).simpleContact(contact);
        }


        if (contact.getFixtureA().getBody().getUserData() != null && contact.getFixtureB().getBody().getUserData() != null) {
            ((Entity)contact.getFixtureA().getBody().getUserData()).collide((Entity) contact.getFixtureB().getBody().getUserData());
            ((Entity)contact.getFixtureB().getBody().getUserData()).collide((Entity) contact.getFixtureA().getBody().getUserData());
        }
    }

    @Override
    public void endContact(Contact contact) {

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}