package com.jef.sn.domain;

import static com.jef.sn.domain.DocumentTestSamples.*;
import static com.jef.sn.domain.TechnicienTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.jef.sn.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class DocumentTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Document.class);
        Document document2 = new Document();
    }
}
