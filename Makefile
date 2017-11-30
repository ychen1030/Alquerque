# This makefile is defined to give you the following targets:
#
#    default: The default target: Compiles the program in package db61b.
#    style: Run our style checker on the project source files.  Requires that
#           the source files compile.
#    check: Compiles the db61b package, if needed, and then performs the
#           tests described in testing/Makefile.
#    clean: Remove regeneratable files (such as .class files) produced by
#           other targets and Emacs backup files.
#
# In other words, type 'make' to compile everything; 'make check' to 
# compile and test everything, and 'make clean' to clean things up.
# 
# You can use this file without understanding most of it, of course, but
# I strongly recommend that you try to figure it out, and where you cannot,
# that you ask questions.  The 'Resources -> Javac and make' link contains
# more documentation.

# Name of package containing main procedure 
PACKAGE = qirkat

# Targets that don't correspond to files, but are to be treated as commands.
.PHONY: default check clean style pre-style

default:
	$(MAKE) -C $(PACKAGE) default

check:
	$(MAKE) -C $(PACKAGE) check

unit:
	$(MAKE) -C $(PACKAGE) unit

integration:
	$(MAKE) -C $(PACKAGE) integration

style:
	$(MAKE) -C $(PACKAGE) style

# This target ignores errors caused by // comments, trailing comments, and
# empty statements.
pre-style:
	$(MAKE) -C $(PACKAGE) pre-style

# 'make clean' will clean up stuff you can reconstruct.
clean:
	$(RM) *~ 
	$(MAKE) -C $(PACKAGE) clean
	$(MAKE) -C testing clean


