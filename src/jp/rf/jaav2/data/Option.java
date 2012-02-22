package jp.rf.jaav2.data;

import java.util.Iterator;

import jp.rf.jaav2.fun.Fun;


public abstract class Option<T> implements Functor<T>, Foldable<T>, Iterable<T> {
	public static interface Match<T, R> {
		R none();
		R some(T x);
	}
	public abstract <R> R match(Match<T, R> f);
	
	public static <T> Option<T> none() {
		return new Option<T>() {
			@Override
			public <R> R match(Match<T, R> f) {
				return f.none();
			}
		};
	}
	public static <T> Option<T> some(final T x) {
		return new Option<T>() {
			@Override
			public <R> R match(Match<T, R> f) {
				return f.some(x);
			}
		};
	}
	public static <T> Option<T> option(final T x) {
		return new Option<T>() {
			@Override
			public <R> R match(Match<T, R> f) {
				return x == null ? f.none() : f.some(x);
			}
		};
	}
	
	public boolean isDefined() {
		return match(new Match<T, Boolean>() {
			@Override
			public Boolean none() {
				return false;
			}
			@Override
			public Boolean some(T x) {
				return true;
			}
		});
	}
	public T getOrElse(final T def) {
		return match(new Match<T, T>() {
			@Override
			public T none() {
				return def;
			}
			@Override
			public T some(T x) {
				return x;
			}
		});
	}
	
	@Override
	public <R> Option<R> fmap(final Fun<T, R> f) {
		return match(new Match<T, Option<R>>() {
			@Override
			public Option<R> none() {
				return Option.none();
			}
			@Override
			public Option<R> some(T x) {
				return Option.some(f._(x));
			}
		});
	}
	@Override
	public <R> R foldr(final Fun<T, Fun<R, R>> f, final R def) {
		return match(new Match<T, R>() {
			@Override
			public R none() {
				return def;
			}
			@Override
			public R some(T x) {
				return f._(x)._(def);
			}
		});
	}
	@Override
	public Iterator<T> iterator() {
		return match(new Match<T, Iterator<T>>() {
			@Override
			public Iterator<T> none() {
				return new Iterator<T>() {
					@Override
					public boolean hasNext() {
						return false;
					}
					@Override
					public T next() {
						return null;
					}
					@Override
					public void remove() {
					}
				};
			}
			@Override
			public Iterator<T> some(final T x) {
				return new Iterator<T>() {
					boolean isFirst = true;
					@Override
					public boolean hasNext() {
						return isFirst;
					}
					@Override
					public T next() {
						isFirst = false;
						return x;
					}
					@Override
					public void remove() {
					}
				};
			}
		});
	}
}
