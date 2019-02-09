# Contributing Guidelines

Firstly, thank you for you interest in contributing to this project!

## Code style

This project follows a set code style across all of the projects. In general, we adere to the [Google code style for Java](https://google.github.io/styleguide/javaguide.html) but in some cases we have our own additions or modifications.

1. In contrast to [4.1.1](https://google.github.io/styleguide/javaguide.html#s4.1.1-braces-always-used), we allow the use of single line `if` statements as long as it not part of a multi-block statement. If the statement consists of multiple `if` branches or an `else` block then braces are used.
2. We're not savages so for that reason we use four spaces instead of two. In reference to [4.2](https://google.github.io/styleguide/javaguide.html#s4.2-block-indentation).
3. As per [2.3.3](https://google.github.io/styleguide/javaguide.html#s2.3.3-non-ascii-characters), we use unicode escapes coupled with a explanatory comment
4. We add an additional blank line after the class header for clarity i.e. a line after `public class ClassName {`
5. As per [3.1.1](https://google.github.io/styleguide/javaguide.html#s3.3.1-wildcard-imports), wildcard imports **are not** used.
6. While we adere to [4.1.3](https://google.github.io/styleguide/javaguide.html#s4.1.3-braces-empty-blocks), empty blocks are **not** to be used without good reason. With any empty block, an appropriate comment explaining why the block is empty is required.
7. In contrast to [4.4](https://google.github.io/styleguide/javaguide.html#s4.4-column-limit), we do not have a mandatory line limit. We ask code-writers us their common sense to determine when a line is too long. Lines over 120-130 characters long should be reviewed to see if they can be line-wrapper. However, as stated before this is not required. 
   1. Use the guidelines at [4.5](https://google.github.io/styleguide/javaguide.html#s4.5-line-wrapping) to determine how to line wrap.
