package org.ignis.javaMud.Mud.Callables;

public interface ObjectCallable<T,P> {
	public T call(P target);
}
