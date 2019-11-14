package com.example.letschat

data class Friends (val uid : String,
                    val name : String,
                    val lastSeen : String?,
                    val profileImage : String) {

    override fun equals(other: Any?): Boolean {
        if(other is Friends) {
            if(other.uid.equals(this.uid))
                return true
        }

        return false
    }
}