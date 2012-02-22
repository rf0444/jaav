package jp.rf.jaav2.data;

import java.util.Iterator;

import jp.rf.jaav2.fun.Fun;

public abstract class List<T> implements Functor<T>, Foldable<T>, Iterable<T> {
	public static interface Match<T, R> {
		R nil();
		R cons(T head, List<T> tail);
	}
	public abstract <R> R match(Match<T, R> f);
	
	public static <T> List<T> nil() {
		return new List<T>() {
			@Override
			public <R> R match(Match<T, R> f) {
				return f.nil();
			}
		};
	}
	public static <T> List<T> cons(final T head, final List<T> tail) {
		return new List<T>() {
			@Override
			public <R> R match(Match<T, R> f) {
				return f.cons(head, tail);
			}
		};
	}
	
	public static <T> List<T> from(final Iterable<T> xs) {
		return from(xs.iterator());
	}
	public static <T> List<T> from(final Iterator<T> it) {
		return new List<T>() {
			@Override
			public <R> R match(Match<T, R> f) {
				return it.hasNext() ? f.cons(it.next(), from(it)) : f.nil();
			}
		};
	}
	
	public Option<T> headOption() {
		return match(new Match<T, Option<T>>() {
			@Override
			public Option<T> nil() {
				return Option.none();
			}
			@Override
			public Option<T> cons(T head, List<T> tail) {
				return Option.some(head);
			}
		});
	}
	public List<T> tail() {
		return match(new Match<T, List<T>>() {
			@Override
			public List<T> nil() {
				return List.this;
			}
			@Override
			public List<T> cons(T head, List<T> tail) {
				return tail;
			}
		});
	}
	
	@Override
	public <R> List<R> fmap(final Fun<T, R> f) {
		return foldr(new Fun<T, Fun<List<R>, List<R>>>() {
			@Override
			public Fun<List<R>, List<R>> _(final T x) {
				return new Fun<List<R>, List<R>>() {
					@Override
					public List<R> _(List<R> xs) {
						return cons(f._(x), xs);
					}
				};
			}
		}, List.<R>nil());
	}
	@Override
	public <R> R foldr(final Fun<T, Fun<R, R>> f, final R def) {
		return match(new Match<T, R>() {
			@Override
			public R nil() {
				return def;
			}
			@Override
			public R cons(T head, List<T> tail) {
				return f._(head)._(tail.foldr(f, def));
			}
		});
	}
	@Override
	public Iterator<T> iterator() {
		return new Iterator<T>() {
			private List<T> current = List.this;
			@Override
			public boolean hasNext() {
				return current.match(new Match<T, Boolean>() {
					@Override
					public Boolean nil() {
						return true;
					}
					@Override
					public Boolean cons(T head, List<T> tail) {
						return false;
					}
				});
			}
			@Override
			public T next() {
				T ret = current.match(new Match<T, T>() {
					@Override
					public T nil() {
						return null;
					}
					@Override
					public T cons(T head, List<T> tail) {
						return head;
					}
				});
				current = current.match(new Match<T, List<T>>() {
					@Override
					public List<T> nil() {
						return current;
					}
					@Override
					public List<T> cons(T head, List<T> tail) {
						return tail;
					}
				});
				return ret;
			}
			@Override
			public void remove() {
			}
		};
	}
}
