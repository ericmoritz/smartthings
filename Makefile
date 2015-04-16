BUILDDIR = dist
SOURCEDIR = src

SOURCES = $(wildcard $(SOURCEDIR)/*.groovy $(SOURCEDIR))
OUTFILES = $(patsubst $(SOURCEDIR)/%.groovy, $(BUILDDIR)/%.groovy, $(SOURCES))

TESTBUILDDIR = dist/test
TESTSOURCEDIR = test
TESTSOURCES = $(wildcard $(TESTSOURCEDIR)/*.groovy $(TESTSOURCEDIR))
TESTOUTFILES = $(patsubst $(TESTSOURCEDIR)/%.groovy, $(TESTBUILDDIR)/%.groovy, $(TESTSOURCES))

.PHONY: all clean setup test

all: $(BUILDDIR)/$(OUTFILES) $(TESTBUILDDIR)/$(TESTOUTFILES) test

clean:
	rm -rf $(BUILDDIR)

test: $(TESTBUILDDIR)/$(TESTOUTFILES)
	find $(TESTBUILDDIR) -iname *.groovy -exec groovy {} \;

$(BUILDDIR):
	mkdir -p $(BUILDDIR)

$(TESTBUILDDIR):
	mkdir -p $(TESTBUILDDIR)


$(BUILDDIR)/%.groovy: $(SOURCEDIR)/%.groovy
	# tag the output file with the project.ttl
	cat project.ttl | sed  's/^\(.\)/\/\/ \1/g' > $@
	gcc -Iinclude/ -P -C -xc -E $< >> $@

$(TESTBUILDDIR)/%.groovy: $(TESTSOURCEDIR)/%.groovy
	gcc -Iinclude/ -P -C -xc -E $< > $@
