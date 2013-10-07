package com.mplify.resources

class ResourceHelpersInGroovy {
    
    /**
     * Slurping the data; returns the lines
     */

    static List<String> load(String fqInputResourceName) {
        InputStream stream = ResourceHelpers.getStreamFromResource(fqInputResourceName)
        List<String> res = new LinkedList()
        try {
            stream.eachLine { res << it }
        }
        finally {
            stream.close()
        }
        return res
    }
}
