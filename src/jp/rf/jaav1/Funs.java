package jp.rf.jaav1;

public final class Funs {
	private Funs() {}
	
	public static <T> Fun<T, T> id() {
		return new Fun<T, T>() {
			@Override
			public T _(T x) {
				return x;
			}
		};
	}
	
	public static <T1, T2, T3> Fun<Fun<T1, Fun<T2, T3>>, Fun<T2, Fun<T1, T3>>> flip() {
		return new Fun<Fun<T1,Fun<T2,T3>>, Fun<T2,Fun<T1,T3>>>() {
			@Override
			public Fun<T2, Fun<T1, T3>> _(final Fun<T1, Fun<T2, T3>> f) {
				return new Fun<T2, Fun<T1, T3>>() {
					@Override
					public Fun<T1, T3> _(final T2 x) {
						return new Fun<T1, T3>() {
							@Override
							public T3 _(T1 y) {
								return f._(y)._(x);
							}
						};
					}
				};
			}
		};
	}
	
	public static <T1, T2, T3> Fun<Fun<T2, T3>, Fun<Fun<T1, T2>, Fun<T1, T3>>> compose() {
		return new Fun<Fun<T2,T3>, Fun<Fun<T1,T2>,Fun<T1,T3>>>() {
			@Override
			public Fun<Fun<T1, T2>, Fun<T1, T3>> _(final Fun<T2, T3> f1) {
				return new Fun<Fun<T1,T2>, Fun<T1,T3>>() {
					@Override
					public Fun<T1, T3> _(final Fun<T1, T2> f2) {
						return new Fun<T1, T3>() {
							@Override
							public T3 _(T1 x) {
								return f1._(f2._(x));
							}
						};
					}
				};
			}
		};
	}
	
	public static <T1, T2> Fun<Fun<? super T1, ? extends T2>, Fun<T1, T2>> cast() {
		return new Fun<Fun<? super T1,? extends T2>, Fun<T1,T2>>() {
			@Override
			public Fun<T1, T2> _(final Fun<? super T1, ? extends T2> f) {
				return new Fun<T1, T2>() {
					@Override
					public T2 _(T1 x) {
						return f._(x);
					}
				};
			}
		};
	}
}
