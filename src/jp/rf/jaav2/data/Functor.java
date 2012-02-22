package jp.rf.jaav2.data;

import jp.rf.jaav2.fun.Fun;

public interface Functor<T> {
	<R> Functor<R> fmap(Fun<T, R> f);
}
