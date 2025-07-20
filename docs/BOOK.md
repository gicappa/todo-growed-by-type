# Growing Software Guided By Types

## Introduction
I've always been thinking that learning happens through different path but the most
effective is pain. Something goes wrong, this something cause pain and I learn that 
I should do it in a different way. The difficult part is that pain tells me what to 
avoid, not what is the best way of doing something. The good practices become evident
only in a darwinian way, error after error becoming the only possible good path left. 

This book is a distilled of the experience of thirty years of writing software, sometimes 
bad software somtimes better, but always eventually working.
Some of the things I learned

- One of the books that shaped my ideas on how to design an application is Growing object-oriented software guided by tests. Together with concepts coming from DDD and common practices I've found a way to design software in an OOP way.
- This way is quite good at separating the business logic from the concrete details of the program (like communicating with the user printing on the screen or decoupling with a message queue a communication of two modules )but sometimes, when a new feature is requested by the customer a bigger refactoring is asked and a a big leap forces the programmer to modify the design so much that is not an evolution anymore, is a kind of rewriting. This seems to be due to the fact that with this way of designing the unit tests are creating some invariants. Invariants of the values that can be set in the data structure. These kind of invariants that can be described only at runtime running parts of the code and checking that the values are set in a certain way respecting unspoken rules. There could be another way to create invariants. It could be possible to use the type system. In the same way the typesystem is creating invariants in the type of values we can pass to a method or return from a method, so why can't we use the type system to model how values are set in a variable instead of testing that the value is not happening? Testing can prove that for a certain input a value is following a certain rule but can't prove the correctness of the values in all the cases. With the typesystem instead you can prescribe that the value can't set in a wrong way. 

## TODO 
To test this way to design the code I'm trying to create a todo list application. we will add new requirements both functional and non-functional trying to evolve the application requirement after requirement. 


## Design an application from scratch
### Walking skeleton
One of the great ideas of the GOOS has been the walking skeleton. The idea is to start by creating all the CI/CD part to deliver in production each commit of the new development before writing any actual code. 
The other idea is that all the communication with the external systems needs to be using the real system both in development and in production (as much as possible). If we are using an Oracle database we should not mock the integration testing with it or use an in memory database, we need to use Oracle. 

This rules ends in the idea of mocking only the type you own: you can mock the interface of the adapters that will drive towards the requests to the database but not the implementation with the database, in that case it needs to be a real integration test. 

### Let's start with the basic types
In a TodoList application we can start with the `TodoBoard` type: this is modeling a board where to stick all the post-its with the todo. 
In a todo board we can have a list of todo items. When we create a board the list of items contained in the board is empty. When we create a new item the list must be the same list before adding the list with one item more. This is a specific constraint, like a property that is always true. This is more than a test but an invariant.  

## DDD 
some of the concepts of the DDD will be used in this method of design: 
- bounded context: used to separate modules. A bounded context is where a ubiquitous language holds true and consistent. 
- domain / infrastructure / application as main layer where domain does not depend on anything (in the domain we have a dependency to the basic models of the implementation)

### Issues 
- we need to understand how and when to replicate the models among bounded contexts: more than often the models are sharable with microservices using the same languages. Is it always the case?

- 