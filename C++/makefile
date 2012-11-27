#  Makefile for linking Boost libraries
#    My program uses boost_thread-mt
#    We have to include boost_system-mt otherwise the compiler would complain. 

CC = g++
LD = g++


#  Some explanation
#    -I   specify the header folder
#    -L   specify the libraries folder
#    -l   specify the library to be linked again

CFLAGS  = -c -Wall -pedantic
LDFLAGS = -I/opt/local/include -L/opt/local/lib -lboost_thread-mt -lboost_system-mt


SOURCES    = MCPi.cpp 
OBJECTS    = $(SOURCES:.cpp = .o)
EXECUTABLE = MCPi


all: $(EXECUTABLE)


$(EXECUTABLE): $(OBJECTS)
	$(LD)  $(LDFLAGS) $(OBJECTS)  -o  $@

.cpp.o:
	$(CC)  $(CFLAGS)  $<  -o  $@


clean:
	rm  -rf  *.o  MCPi
