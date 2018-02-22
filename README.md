# Embedded Classification Software Toolbox (ECST)

A software toolbox to train machine learning and classification systems, to compare accuracy and performance, and to analyze the complexity of trained systems.

## Abstract 

Embedded microcontrollers are employed in an increasing number of applications as a target for the implementation of classification systems. This is true, for example, for the fields of sports, automotive, and medical engineering. However, important challenges arise when implementing classification systems on embedded microcontrollers, which is mainly due to limited hardware resources.
With the Embedded Classification Software Toolbox (ECST), we present a solution to the two main challenges, namely obtaining a classification system with low computational complexity and, at the same time, high classification accuracy. For the first challenge, we propose complexity measures on the mathematical operation and parameter level, because the abstraction level of the commonly used Landau notation is too high in the context of embedded system implementation. For the second challenge, we present a software toolbox that trains different classification systems, compares their classification accuracy, and finally analyzes the complexity of the trained system.

## Important Files

* Main class: [ecst.view.ECST](src/ecst/view/ECST.java)
* Configuration file: [config/Algorithms.xml](config/Algorithms.xml)

## Requirements
 
* Apache Commons CLI 1.3.1
* Apache Commons Math 3.3.0
* LIBSVM 3.11
* WEKA 3.6.6

## Citation Request

Please cite these publications when using the ECST

Matthias Ring, Ulf Jensen, Patrick Kugler, Bjoern M. Eskofier. **Software-based performance and complexity analysis for the design of embedded classification systems**. In *Proceedings of the 21st International Conference of Pattern Recognition (ICPR 2012)*, Tsukuba, Japan, pp. 2266-2269, 2012.

Ulf Jensen, Patrick Kugler, Matthias Ring, Bjoern M. Eskofier. **Approaching the accuracy–cost conflict in embedded classification system design**. *Pattern Analysis and Applications*, 19(3), pp. 839–855, 2015.
