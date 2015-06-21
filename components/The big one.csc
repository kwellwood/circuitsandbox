<?xml version="1.0" encoding="ISO-8859-1"?>
<!DOCTYPE customComponent [
   <!ELEMENT customComponent (input+, component*, output+)>
   <!ATTLIST customComponent
       name CDATA #REQUIRED>
   <!ELEMENT input EMPTY>
   <!ATTLIST input
       x CDATA #REQUIRED
       y CDATA #REQUIRED>
   <!ELEMENT component (connection*)>
   <!ATTLIST component
       type CDATA #REQUIRED
       id CDATA #REQUIRED>
   <!ELEMENT connection EMPTY>
   <!ATTLIST connection
       number CDATA #REQUIRED
       sourceId CDATA #REQUIRED
       sourcePin CDATA #REQUIRED>
   <!ELEMENT output EMPTY>
   <!ATTLIST output
       x CDATA #REQUIRED
       y CDATA #REQUIRED
       sourceId CDATA #REQUIRED
       sourcePin CDATA #REQUIRED>
]><customComponent name="The Big One">
    <input x="0" y="10" />
    <input x="0" y="20" />
    <input x="0" y="30" />
    <input x="0" y="40" />
    <input x="0" y="50" />
    <input x="0" y="60" />
    <input x="0" y="70" />
    <input x="0" y="80" />
    <input x="0" y="90" />
    <input x="0" y="100" />
    <input x="0" y="110" />
    <input x="0" y="120" />
    <input x="0" y="130" />
    <input x="0" y="140" />
    <input x="0" y="150" />
    <input x="0" y="160" />
    <output x="169" y="10" sourceId="-1" sourcePin="0" />
    <output x="169" y="20" sourceId="-1" sourcePin="1" />
    <output x="169" y="30" sourceId="-1" sourcePin="2" />
    <output x="169" y="40" sourceId="-1" sourcePin="3" />
    <output x="169" y="50" sourceId="-1" sourcePin="4" />
    <output x="169" y="60" sourceId="-1" sourcePin="5" />
    <output x="169" y="70" sourceId="-1" sourcePin="6" />
    <output x="169" y="80" sourceId="-1" sourcePin="7" />
    <output x="169" y="90" sourceId="-1" sourcePin="8" />
    <output x="169" y="100" sourceId="-1" sourcePin="9" />
    <output x="169" y="110" sourceId="-1" sourcePin="10" />
    <output x="169" y="120" sourceId="-1" sourcePin="11" />
    <output x="169" y="130" sourceId="-1" sourcePin="12" />
    <output x="169" y="140" sourceId="-1" sourcePin="13" />
    <output x="169" y="150" sourceId="-1" sourcePin="14" />
    <output x="169" y="160" sourceId="-1" sourcePin="15" />
</customComponent>
