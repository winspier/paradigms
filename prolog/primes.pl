min_div_table(2, 2).

init(MAX_N) :- find_primes(2, MAX_N).

find_primes(I, N) :-
    mark_prime(I, N);
    I * I < N,
    I1 is I + 1,
    find_primes(I1, N).

prime(A) :- primes(A), !.
prime(A) :- number(A), A =\= 1, \+ composite(A), assertz(primes(A)).
min_prime_div(P, P) :- prime(P), !.
min_prime_div(N, P) :- min_div_table(N, P), !.

mark_composite(N, _) :- min_div_table(N, _), !.
mark_composite(N, I) :- assertz(min_div_table(N, I)), assertz(composite(N)), !.

mark_by_step(V, P, N) :-
    V < N,
    mark_composite(V, P),
    V1 is V + P,
    mark_by_step(V1, P, N).

mark_prime(I, N) :-
    prime(I),
    I2 is I * I,
    mark_by_step(I2, I, N).

prime_divisors(1, []) :- !.
prime_divisors(N, [H | T]) :-
    number(N), !,
    min_prime_div(N, H),
    N1 is N / H,
    prime_divisors(N1, T).

prime_divisors(N, L) :-
    list(L), !,
    find_value_by_divisors(L, 2, N).

find_value_by_divisors([], _, 1) :- !.
find_value_by_divisors([H | T], P, V) :-
    P =< H,
    find_value_by_divisors(T, H, R),
    V is R * H.

lcm(A, A, A) :- !.
lcm(A, B, LCM) :- operation_of_divs(A, B, merged_list, LCM).

gcd(A, A, A) :- !.
gcd(A, B, GCD) :- operation_of_divs(A, B, common_list, GCD).

operation_of_divs(A, B, F, ANS) :-
    number(A), number(B),
    prime_divisors(A, D1),
    prime_divisors(B, D2),
    G =.. [F, D1, D2, R], call(G), !,
    find_value_by_divisors(R, 2, ANS).

common_list([], _, []).
common_list(_, [], []).
common_list([H | T1], [H | T2], [H | T3]) :- common_list(T1, T2, T3).
common_list([H1 | T1], [H2 | T2], R) :- H1 < H2,! , common_list(T1, [H2 | T2], R).
common_list([H1 | T1], [H2 | T2], R) :- H1 > H2,! , common_list([H1 | T1], T2, R).

merged_list([], L, L).
merged_list(L, [], L).
merged_list([H | T1], [H | T2], [H | T3]) :- merged_list(T1, T2, T3).
merged_list([H | T1], [H2 | T2], [H | T3]) :- H < H2, !, merged_list(T1, [H2 | T2], T3).
merged_list([H1 | T1], [H | T2], [H | T3]) :- H1 > H, !, merged_list([H1 | T1], T2, T3).