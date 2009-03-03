/*
 * Copyright 2004-2008 the Seasar Foundation and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.seasar.dbflute.helper.jdbc.metadata.comment;

import java.util.Map;
import java.util.Set;

/**
 * @author jflute
 */
public interface DfDbCommentExtractor {

    public Map<String, UserTabComments> extractTableComment(Set<String> tableSet);

    public Map<String, Map<String, UserColComments>> extractColumnComment(Set<String> tableSet);

    public static class UserTabComments {
        protected String tableName;
        protected String comments;

        public boolean hasComments() {
            return comments != null && comments.trim().length() > 0;
        }

        public String getTableName() {
            return tableName;
        }

        public void setTableName(String tableName) {
            this.tableName = tableName;
        }

        public String getComments() {
            return comments;
        }

        public void setComments(String comments) {
            this.comments = comments;
        }
    }

    public static class UserColComments {
        protected String tableName;
        protected String columnName;
        protected String comments;

        public boolean hasComments() {
            return comments != null && comments.trim().length() > 0;
        }

        public String getTableName() {
            return tableName;
        }

        public void setTableName(String tableName) {
            this.tableName = tableName;
        }

        public String getColumnName() {
            return columnName;
        }

        public void setColumnName(String columnName) {
            this.columnName = columnName;
        }

        public String getComments() {
            return comments;
        }

        public void setComments(String comments) {
            this.comments = comments;
        }
    }

}
