package com.zenval.server.digit

import spock.lang.Specification

/**
 * Created by arturo on 20/05/17.
 */
class DigitUniqueControlTest extends Specification {

    def DigitUniqueControl uniqueDigitControl

    def setup() {
        uniqueDigitControl = new DigitUniqueControl()
    }

    def "UniqueDigitControl"() {

        when: "first time"
        def result = uniqueDigitControl.isUnique("A")
        then:
        result == true
        uniqueDigitControl.getProcessedSize() == 1
        uniqueDigitControl.getDuplicatedSize() == 0

        when: "duplicated time"
        result = uniqueDigitControl.isUnique("A")
        then:
        result == false
        uniqueDigitControl.getProcessedSize() == 1
        uniqueDigitControl.getDuplicatedSize() == 1

        when: "new one"
        result = uniqueDigitControl.isUnique("B")
        then:
        result == true
        uniqueDigitControl.getProcessedSize() == 2
        uniqueDigitControl.getDuplicatedSize() == 1
    }
}
