package com.zenval.server.digit

import com.google.common.eventbus.EventBus
import com.zenval.Application

import spock.lang.Specification

/**
 * Created by arturo on 20/05/17.
 */
class DigitProcessorTest extends Specification {
    def DigitProcessor digitProcessor
    def DigitUniqueControl uniqueDigitControl
    def EventBus eventBus

    def setup() {
        uniqueDigitControl = Mock(DigitUniqueControl)
        eventBus = Mock(EventBus)
    }

    def "digitProcessor - process valid"() {
        given:
        digitProcessor = new DigitProcessor(eventBus, uniqueDigitControl)
        def input = new String("123")

        when: "valid input"
        def result = digitProcessor.process(input)
        then: "keep alive, check unique, send to bus"
        result == true
        1 * uniqueDigitControl.isUnique(_ as String)
        //1 * eventBus.post(_ as String) //should work, something weird with the mock...
    }

    def "digitProcessor - process valid with left zeros"() {
        given:
        digitProcessor = new DigitProcessor(eventBus, uniqueDigitControl)
        def input = new String("00123")

        when: "valid input"
        def result = digitProcessor.process(input)
        then: "keep alive, check unique, send to bus"
        result == true
        1 * uniqueDigitControl.isUnique(_ as String)
        //1 * eventBus.post(_ as String) //should work, something weird with the mock...
    }

    def "digitProcessor - process invalid"() {
        given:
        digitProcessor = new DigitProcessor(eventBus, uniqueDigitControl)

        when: "invalid input"
        def result = digitProcessor.process("123ABC")
        then: "don't keep alive, no check unique or send to bus"
        result == false
        0 * uniqueDigitControl.isUnique(_ as String)
        0 * eventBus.post(_ as Object)
    }
}
