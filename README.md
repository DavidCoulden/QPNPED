QPNPED
======

A tool for evaluating the performance of relational databases using Queueing Petri Nets (QPNs). It functions by modelling
database traffic compositions in the form of SQL transactions as a QPN that is then simulated using QPME (http://se.informatik.uni-wuerzburg.de/projects/qpme/).
For more information about Queueing Petri Nets and how they work, see the literature from the previous link.

It currently supports PostgreSQL and models execution using table level non-transactional locking.

Further information about the modelling methodology and the tool architecture can be found in "Evaluating database performance with QPNs.pdf".
