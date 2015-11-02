## Twitter-demo
[Demo video](https://drive.google.com/file/d/0B7L-eLYYr_x9UTRFYXMtY2tmYjA/view?usp=sharing)

### What to look forward to:
- The video upload feature was not trivial to implement. In one screen there is video capturing, video editing using a native C library, four separate network request to be coordinated, handling user input... and all across multiple threads. It was fun to implement! It should be thoroughly tested.
- Well encapsulated form validation (for tweet length). It's a pattern that could scale quite well to other use cases, I think.
- Effective Java fans will be happy to spot items 1, 2, 4, 13, 14, 15, 16, 21, 22, 29, 34, 45, 46, 52 amongst others :)
- [Shank](https://github.com/memoizr/shank)! This is a production-like app that features the use of Shank, my favourite dependency injection framework. Here you can see the way I intended it to be used. There are plenty of examples of how Shank can be used to aid testing (the VideoRecorder class has been made testable thanks to Shank, it would have been much more awkward without it).
- A nice MVP pattern, in which the view is very passive, and very stupid. Thanks [mvarnagiris](https://github.com/mvarnagiris) for showing me the light!
- I included a single BDD scenario, just to illustrate how to use Cucumber in Java. It's a very helpful way of developing software, but for this demo, I opted to go TDD first.

### What not to look forward to:
- Low Cucumber coverage.
- Sparse testing for some util classes, in particular the ones dealing with saving files, setting executable permissions and running native code. I still haven't figured out how to test that.
- Spiders.
