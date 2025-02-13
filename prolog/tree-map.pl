key(node(Key, _, _, _, _, _), Key).
value(node(_, Value, _, _, _, _), Value).
left(node(_, _, Left, _, _, _), Left).
right(node(_, _, _, Right, _, _), Right).
size(node(_, _, _, _, Size, _), Size).
size(null, 0).
count(node(_, _, _, _, _, Count), Count).
count(null, 0).

max(A, B, A) :- A >= B.
max(A, B, B) :- A < B.

calc_cond(node(_, _, L, R, Size, Count)) :-
    size(L, LSZ), size(R, RSZ), max(LSZ, RSZ, S), Size is (S + 1),
    count(L, LC), count(R, RC), Count is (LC + RC + 1).

map_put(null, Key, Value, node(Key, Value, null, null, 1, 1)).
map_put(node(Key, _, L, R, S, C), Key, Value, node(Key, Value, L, R, S, C)).
map_put(node(K, V, L, R, _, _), Key, Value, Result) :-
    K > Key,
    map_put(L, Key, Value, NewL),
    NewT = node(K, V, NewL, R, _, _),
    calc_cond(NewT),
    balance(NewT, Result).
map_put(node(K, V, L, R, _, _), Key, Value, Result) :-
    K < Key,
    map_put(R, Key, Value, NewR),
    NewT = node(K, V, L, NewR, _, _),
    calc_cond(NewT),
    balance(NewT, Result).

get_min(node(K, V, null, _, _, _), (K, V)).
get_min(T, Res) :- left(T, L), get_min(L, Res).

remove_min(node(_, _, null, R, _, _), R).
remove_min(node(K, V, L, R, _, _), Res) :-
    remove_min(L, NewL),
	NewT = node(K, V, NewL, R, _, _),
    calc_cond(NewT),
    balance(NewT, Res).

map_remove(null, _, null).
map_remove(node(K, V, L, R, _, _), Key, Res) :-
    Key < K,
    map_remove(L, Key, NewL),
    NewT = node(K, V, NewL, R, _, _),
    calc_cond(NewT),
    balance(NewT, Res).
map_remove(node(K, V, L, R, _, _), Key, Res) :-
    Key > K,
    map_remove(R, Key, NewR),
    NewT = node(K, V, L, NewR, _, _),
    calc_cond(NewT),
    balance(NewT, Res).
map_remove(node(K, _, L, null, _, _), K, L) :- !.
map_remove(node(K, _, L, R, _, _), K, Res) :-
    get_min(R, (NewK, NewV)),
    remove_min(R, NewR),
    NewT = node(NewK, NewV, L, NewR, _, _),
    calc_cond(NewT),
    balance(NewT, Res).

map_get(node(Key, Value, _, _, _, _), Key, Value) :- !.
map_get(node(K, _, _, R, _, _), Key, Value) :-
    Key > K, !,
    map_get(R, Key, Value).
map_get(node(K, _, L, _, _, _), Key, Value) :-
    Key < K, !,
    map_get(L, Key, Value).

lower_bound(node(K, _, L, _, _, _), Key, Entry) :-
    K >= Key,
    lower_bound(L, Key, Entry), !.
lower_bound(Node, Key, Node) :- key(Node, K), K >= Key.

lower_bound(Map, Key, Entry) :-
    key(Map, K), K < Key, right(Map, R),
    lower_bound(R, Key, Entry).

map_ceilingEntry(Map, Key, (K, V)) :- lower_bound(Map, Key, node(K, V, _, _, _, _)).


count_head(node(K, _, L, R, _, _), Key, Cur, Size) :- K >= Key, count_head(L, Key, Cur, Size).
count_head(node(K, _, L, R, _, _), Key, Cur, Size) :-
    K < Key, count(L, CL), C is CL + Cur + 1, count_head(R, Key, C, Size).
count_head(null, _, Cur, Cur).
map_headMapSize(Map, Key, Size) :- count_head(Map, Key, 0, Size), !.

map_tailMapSize(Map, ToKey, Size) :- count(Map, MapSize), map_headMapSize(Map, ToKey, Sub), Size is (MapSize - Sub).

map_removeCeiling(Map, Key, Result) :-
    map_ceilingEntry(Map, Key, (RemoveKey, _)),
    map_remove(Map, RemoveKey, Result), !.
map_removeCeiling(T, _, T).


bfactor(node(_, _, L, R, _, _), Res) :-
    size(L, SL),
    size(R, SR),
    Res is SR - SL.

map_build(List, Tree) :- build(List, null, Tree).
build([], T, T).
build([(Key, Value) | T], Tree, Result) :-
    map_put(Tree, Key, Value, MidRes),
    build(T, MidRes, Result).


balance(T, T) :- (bfactor(T, 1);bfactor(T, 0);bfactor(T, -1)), !.

balance(node(K, V, L, R, _, _), Result) :-
    bfactor(node(K, V, L, R, _, _), 2),
    bfactor(R, BR), BR < 0, !,
    rotate_right(R, NewR),
    NewT = node(K, V, L, NewR, _, _),
    calc_cond(NewT),
    rotate_left(NewT, Result).
balance(T, Result) :-
    bfactor(T, 2),
    right(T, R),
    bfactor(R, BR), BR >= 0, !,
    rotate_left(T, Result).
balance(node(K, V, L, R, _, _), Result) :-
    bfactor(node(K, V, L, R, _, _), -2),
    bfactor(L, BL), BL > 0, !,
    rotate_left(L, NewL),
    NewT = node(K, V, NewL, R, _, _),
    calc_cond(NewT),
    rotate_right(NewT, Result).
balance(T, Result) :-
    bfactor(T, -2),
    left(T, L),
    bfactor(L, BL), BL =< 0, !,
    rotate_right(T, Result).

rotate_right(node(K, V, L, R, _, _), Q) :-
    P = node(K, V, QR, R, _, _),
    left(L, QL), right(L, QR), key(L, QK), value(L, QV),
    Q = node(QK, QV, QL, P, _, _),
    calc_cond(P), calc_cond(Q).

rotate_left(node(K, V, L, R, _, _), P) :-
    Q = node(K, V, L, PL, _, _),
    left(R, PL), right(R, PR), key(R, PK), value(R, PV),
    P = node(PK, PV, Q, PR, _, _),
    calc_cond(Q), calc_cond(P).
