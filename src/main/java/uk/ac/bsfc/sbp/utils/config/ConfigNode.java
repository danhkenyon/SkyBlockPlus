package uk.ac.bsfc.sbp.utils.config;

import java.util.List;

public sealed interface ConfigNode permits
        ConfigNode.BooleanNode,
        ConfigNode.IntNode,
        ConfigNode.DoubleNode,
        ConfigNode.StringNode,
        ConfigNode.ObjectNode,
        ConfigNode.ListNode {

    String name();
    Class<?> type();
    Object value();

    record BooleanNode(String name, Class<?> type, Boolean value) implements ConfigNode {}
    record IntNode(String name, Class<?> type, Integer value) implements ConfigNode {}
    record DoubleNode(String name, Class<?> type, Double value) implements ConfigNode {}
    record StringNode(String name, Class<?> type, String value) implements ConfigNode {}

    record ObjectNode(String name, Class<?> type, Object value, List<ConfigNode> children)
            implements ConfigNode {}

    record ListNode(String name, Class<?> type, List<String> value)
            implements ConfigNode {}
}
