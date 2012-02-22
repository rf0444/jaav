package jp.rf.jaav2.data;

import jp.rf.jaav2.fun.Fun;

public interface Foldable<T> {
	<R> R foldr(Fun<T, Fun<R, R>> f, R def);
}
