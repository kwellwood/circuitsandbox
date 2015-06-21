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
]><customComponent name="Full Adder">
    <input x="0" y="10" />
    <input x="0" y="30" />
    <input x="0" y="50" />
    <component type="xor2" id="4">
        <connection number="0" sourceId="-1" sourcePin="0" />
        <connection number="1" sourceId="-1" sourcePin="1" />
    </component>
    <component type="or2" id="23">
        <connection number="0" sourceId="28" sourcePin="0" />
        <connection number="1" sourceId="5" sourcePin="0" />
    </component>
    <component type="and2" id="28">
        <connection number="0" sourceId="4" sourcePin="0" />
        <connection number="1" sourceId="-1" sourcePin="2" />
    </component>
    <component type="xor2" id="35">
        <connection number="0" sourceId="4" sourcePin="0" />
        <connection number="1" sourceId="-1" sourcePin="2" />
    </component>
    <component type="and2" id="5">
        <connection number="0" sourceId="-1" sourcePin="0" />
        <connection number="1" sourceId="-1" sourcePin="1" />
    </component>
    <output x="70" y="20" sourceId="35" sourcePin="0" />
    <output x="70" y="40" sourceId="23" sourcePin="0" />
</customComponent>
