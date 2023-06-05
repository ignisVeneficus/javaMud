package org.ignis.javaMud.Mud.data;

import java.util.Set;

import org.ignis.javaMud.Mud.Callables.ObjectCallable;
import org.ignis.javaMud.Mud.Core.Entity;
/**
 * Olyan statuszok, amik idovel elmulnak pl, eges, feny, stb., ami az adott objektumhoz tartozik
 * @author Ignis
 *
 */
// https://stackoverflow.com/questions/4685563/how-to-pass-a-function-as-a-parameter-in-java
public class StatusEffect {
	private int remainderTime;
	private String name;
	private Set<String> effects;
	private ObjectCallable<Boolean,Entity> handler;
}
