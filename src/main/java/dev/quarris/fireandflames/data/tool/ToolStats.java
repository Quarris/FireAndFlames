package dev.quarris.fireandflames.data.tool;

public record ToolStats(float durabilityModifier, float miningSpeedModifier, float damage, float attackSpeed, int durabilityLossPerBlock) {

    public static class Builder {
        private float durabilityModifier = 1.0f;
        private float miningSpeedModifier = 1.0f;
        private float damage = 0.0f;
        private float attackSpeed = 0.0f;
        private int durabilityLossPerBlock = 1;

        public Builder() {
        }

        public Builder durabilityModifier(float durabilityModifier) {
            this.durabilityModifier = durabilityModifier;
            return this;
        }

        public Builder miningSpeedModifier(float speedModifier) {
            this.miningSpeedModifier = speedModifier;
            return this;
        }

        public Builder damage(float damage) {
            this.damage = damage;
            return this;
        }

        public Builder baseAttackSpeed(float baseAttackSpeed) {
            this.attackSpeed = baseAttackSpeed;
            return this;
        }

        public Builder durabilityLossPerBlock(int loss) {
            this.durabilityLossPerBlock = loss;
            return this;
        }

        public ToolStats build() {
            return new ToolStats(this.durabilityModifier, this.miningSpeedModifier, this.damage, this.attackSpeed, this.durabilityLossPerBlock);
        }
    }
}
