package jp.rf.jaav1;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;

public abstract class Option<T> implements Iterable<T> {
	private Option() {}
	
	abstract <T2> Option<T2> flatMap(Fun<T, Option<T2>> f);
	abstract T getOrElse(T x);
	
	static final class Some<T> extends Option<T> {
		final T value;
		Some(T value) {
			this.value = value;
		}
		@Override
		<T2> Option<T2> flatMap(Fun<T, Option<T2>> f) {
			return f._(value);
		}
		@Override
		T getOrElse(T _) {
			return value;
		}
		@Override @SuppressWarnings("unchecked")
		public Iterator<T> iterator() {
			return Arrays.asList(value).iterator();
		}
		@Override
		public int hashCode() {
			return value.hashCode();
		}
		@Override
		public boolean equals(Object obj) {
			if (obj == null) {
				return false;
			}
			if (!(obj instanceof Some<?>)) {
				return false;
			}
			Some<?> that = (Some<?>) obj;
			return this.value.equals(that.value);
		}
		@Override
		public String toString() {
			return String.format("Some(%s)", value);
		}
	}
	
	static final class None<T> extends Option<T> {
		@Override
		<T2> Option<T2> flatMap(Fun<T, Option<T2>> f) {
			return None();
		}
		@Override
		T getOrElse(T value) {
			return value;
		}
		@Override
		public Iterator<T> iterator() {
			return Collections.<T>emptyList().iterator();
		}
		@Override
		public boolean equals(Object obj) {
			if (obj == null) return false;
			return obj instanceof None<?>;
		}
		@Override
		public int hashCode() {
			return 42;
		}
		@Override
		public String toString() {
			return "None";
		}
	}
	
	public static <T> Fun<T, Option<T>> Some() {
		return new Fun<T, Option<T>>() {
			@Override
			public Option<T> _(T x) {
				return new Some<T>(x);
			}
		};
	}
	
	public static <T> Option<T> None() {
		return new None<T>();
	}
	
	public static <T> Fun<Option<T>, Boolean> isDefined() {
		return new Fun<Option<T>, Boolean>() {
			@Override
			public Boolean _(Option<T> x) {
				return x instanceof Some<?>;
			}
		};
	}
	
	public static <T> Fun<T, Fun<Option<T>, T>> getOrElse() {
		return new Fun<T, Fun<Option<T>,T>>() {
			@Override
			public Fun<Option<T>, T> _(final T value) {
				return new Fun<Option<T>, T>() {
					@Override
					public T _(Option<T> x) {
						return x.getOrElse(value);
					}
				};
			}
		};
	}
	
	public static <T> Fun<T, Option<T>> unit() {
		return Some();
	}
	
	public static <T1, T2> Fun<Fun<T1, Option<T2>>, Fun<Option<T1>, Option<T2>>> bind() {
		return new Fun<Fun<T1,Option<T2>>, Fun<Option<T1>,Option<T2>>>() {
			@Override
			public Fun<Option<T1>, Option<T2>> _(final Fun<T1, Option<T2>> f) {
				return new Fun<Option<T1>, Option<T2>>() {
					@Override
					public Option<T2> _(Option<T1> x) {
						return x.flatMap(f);
					}
				};
			}
		};
	}
	
	public static <T> Fun<Option<Option<T>>, Option<T>> join() {
		return Option.<Option<T>, T>bind()._(Funs.<Option<T>>id());
	}
	
	public static <T1, T2> Fun<Fun<T1, T2>, Fun<Option<T1>, Option<T2>>> map() {
		return new Fun<Fun<T1,T2>, Fun<Option<T1>,Option<T2>>>() {
			@Override
			public Fun<Option<T1>, Option<T2>> _(final Fun<T1, T2> f) {
				return Option.<T1, T2>bind()._(Funs.<T1, T2, Option<T2>>compose()._(Option.<T2>unit())._(f));
			}
		};
	}
	
	public static <T1, T2> Fun<T2, Fun<Fun<T1, T2>, Fun<Option<T1>, T2>>> maybe() {
		return new Fun<T2, Fun<Fun<T1,T2>,Fun<Option<T1>,T2>>>() {
			@Override
			public Fun<Fun<T1, T2>, Fun<Option<T1>, T2>> _(final T2 x) {
				return new Fun<Fun<T1,T2>, Fun<Option<T1>,T2>>() {
					@Override
					public Fun<Option<T1>, T2> _(final Fun<T1, T2> f) {
						return Funs.<Option<T1>, Option<T2>, T2>compose()._(Option.<T2>getOrElse()._(x))._(Option.<T1, T2>map()._(f));
					}
				};
			}
		};
	}
	
	public static <T1, T2 extends T1> Fun<Option<T2>, Option<T1>> cast() {
		return Option.<T2, T1>map()._(Funs.<T2, T1>cast()._(Funs.<T2>id()));
	}
}
