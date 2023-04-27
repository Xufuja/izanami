// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: scene_file.proto

package dev.xfj.protobuf;

/**
 * Protobuf type {@code dev.xfj.protobuf.TransformComponent}
 */
public final class TransformComponent extends
    com.google.protobuf.GeneratedMessageV3 implements
    // @@protoc_insertion_point(message_implements:dev.xfj.protobuf.TransformComponent)
    TransformComponentOrBuilder {
private static final long serialVersionUID = 0L;
  // Use TransformComponent.newBuilder() to construct.
  private TransformComponent(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
    super(builder);
  }
  private TransformComponent() {
  }

  @java.lang.Override
  @SuppressWarnings({"unused"})
  protected java.lang.Object newInstance(
      UnusedPrivateParameter unused) {
    return new TransformComponent();
  }

  public static final com.google.protobuf.Descriptors.Descriptor
      getDescriptor() {
    return dev.xfj.protobuf.SceneFile.internal_static_dev_xfj_protobuf_TransformComponent_descriptor;
  }

  @java.lang.Override
  protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internalGetFieldAccessorTable() {
    return dev.xfj.protobuf.SceneFile.internal_static_dev_xfj_protobuf_TransformComponent_fieldAccessorTable
        .ensureFieldAccessorsInitialized(
            dev.xfj.protobuf.TransformComponent.class, dev.xfj.protobuf.TransformComponent.Builder.class);
  }

  public static final int TRANSLATION_FIELD_NUMBER = 1;
  private dev.xfj.protobuf.Vector3f translation_;
  /**
   * <code>.dev.xfj.protobuf.Vector3f translation = 1;</code>
   * @return Whether the translation field is set.
   */
  @java.lang.Override
  public boolean hasTranslation() {
    return translation_ != null;
  }
  /**
   * <code>.dev.xfj.protobuf.Vector3f translation = 1;</code>
   * @return The translation.
   */
  @java.lang.Override
  public dev.xfj.protobuf.Vector3f getTranslation() {
    return translation_ == null ? dev.xfj.protobuf.Vector3f.getDefaultInstance() : translation_;
  }
  /**
   * <code>.dev.xfj.protobuf.Vector3f translation = 1;</code>
   */
  @java.lang.Override
  public dev.xfj.protobuf.Vector3fOrBuilder getTranslationOrBuilder() {
    return translation_ == null ? dev.xfj.protobuf.Vector3f.getDefaultInstance() : translation_;
  }

  public static final int ROTATION_FIELD_NUMBER = 2;
  private dev.xfj.protobuf.Vector3f rotation_;
  /**
   * <code>.dev.xfj.protobuf.Vector3f rotation = 2;</code>
   * @return Whether the rotation field is set.
   */
  @java.lang.Override
  public boolean hasRotation() {
    return rotation_ != null;
  }
  /**
   * <code>.dev.xfj.protobuf.Vector3f rotation = 2;</code>
   * @return The rotation.
   */
  @java.lang.Override
  public dev.xfj.protobuf.Vector3f getRotation() {
    return rotation_ == null ? dev.xfj.protobuf.Vector3f.getDefaultInstance() : rotation_;
  }
  /**
   * <code>.dev.xfj.protobuf.Vector3f rotation = 2;</code>
   */
  @java.lang.Override
  public dev.xfj.protobuf.Vector3fOrBuilder getRotationOrBuilder() {
    return rotation_ == null ? dev.xfj.protobuf.Vector3f.getDefaultInstance() : rotation_;
  }

  public static final int SCALE_FIELD_NUMBER = 3;
  private dev.xfj.protobuf.Vector3f scale_;
  /**
   * <code>.dev.xfj.protobuf.Vector3f scale = 3;</code>
   * @return Whether the scale field is set.
   */
  @java.lang.Override
  public boolean hasScale() {
    return scale_ != null;
  }
  /**
   * <code>.dev.xfj.protobuf.Vector3f scale = 3;</code>
   * @return The scale.
   */
  @java.lang.Override
  public dev.xfj.protobuf.Vector3f getScale() {
    return scale_ == null ? dev.xfj.protobuf.Vector3f.getDefaultInstance() : scale_;
  }
  /**
   * <code>.dev.xfj.protobuf.Vector3f scale = 3;</code>
   */
  @java.lang.Override
  public dev.xfj.protobuf.Vector3fOrBuilder getScaleOrBuilder() {
    return scale_ == null ? dev.xfj.protobuf.Vector3f.getDefaultInstance() : scale_;
  }

  private byte memoizedIsInitialized = -1;
  @java.lang.Override
  public final boolean isInitialized() {
    byte isInitialized = memoizedIsInitialized;
    if (isInitialized == 1) return true;
    if (isInitialized == 0) return false;

    memoizedIsInitialized = 1;
    return true;
  }

  @java.lang.Override
  public void writeTo(com.google.protobuf.CodedOutputStream output)
                      throws java.io.IOException {
    if (translation_ != null) {
      output.writeMessage(1, getTranslation());
    }
    if (rotation_ != null) {
      output.writeMessage(2, getRotation());
    }
    if (scale_ != null) {
      output.writeMessage(3, getScale());
    }
    getUnknownFields().writeTo(output);
  }

  @java.lang.Override
  public int getSerializedSize() {
    int size = memoizedSize;
    if (size != -1) return size;

    size = 0;
    if (translation_ != null) {
      size += com.google.protobuf.CodedOutputStream
        .computeMessageSize(1, getTranslation());
    }
    if (rotation_ != null) {
      size += com.google.protobuf.CodedOutputStream
        .computeMessageSize(2, getRotation());
    }
    if (scale_ != null) {
      size += com.google.protobuf.CodedOutputStream
        .computeMessageSize(3, getScale());
    }
    size += getUnknownFields().getSerializedSize();
    memoizedSize = size;
    return size;
  }

  @java.lang.Override
  public boolean equals(final java.lang.Object obj) {
    if (obj == this) {
     return true;
    }
    if (!(obj instanceof dev.xfj.protobuf.TransformComponent)) {
      return super.equals(obj);
    }
    dev.xfj.protobuf.TransformComponent other = (dev.xfj.protobuf.TransformComponent) obj;

    if (hasTranslation() != other.hasTranslation()) return false;
    if (hasTranslation()) {
      if (!getTranslation()
          .equals(other.getTranslation())) return false;
    }
    if (hasRotation() != other.hasRotation()) return false;
    if (hasRotation()) {
      if (!getRotation()
          .equals(other.getRotation())) return false;
    }
    if (hasScale() != other.hasScale()) return false;
    if (hasScale()) {
      if (!getScale()
          .equals(other.getScale())) return false;
    }
    if (!getUnknownFields().equals(other.getUnknownFields())) return false;
    return true;
  }

  @java.lang.Override
  public int hashCode() {
    if (memoizedHashCode != 0) {
      return memoizedHashCode;
    }
    int hash = 41;
    hash = (19 * hash) + getDescriptor().hashCode();
    if (hasTranslation()) {
      hash = (37 * hash) + TRANSLATION_FIELD_NUMBER;
      hash = (53 * hash) + getTranslation().hashCode();
    }
    if (hasRotation()) {
      hash = (37 * hash) + ROTATION_FIELD_NUMBER;
      hash = (53 * hash) + getRotation().hashCode();
    }
    if (hasScale()) {
      hash = (37 * hash) + SCALE_FIELD_NUMBER;
      hash = (53 * hash) + getScale().hashCode();
    }
    hash = (29 * hash) + getUnknownFields().hashCode();
    memoizedHashCode = hash;
    return hash;
  }

  public static dev.xfj.protobuf.TransformComponent parseFrom(
      java.nio.ByteBuffer data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static dev.xfj.protobuf.TransformComponent parseFrom(
      java.nio.ByteBuffer data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static dev.xfj.protobuf.TransformComponent parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static dev.xfj.protobuf.TransformComponent parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static dev.xfj.protobuf.TransformComponent parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static dev.xfj.protobuf.TransformComponent parseFrom(
      byte[] data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static dev.xfj.protobuf.TransformComponent parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static dev.xfj.protobuf.TransformComponent parseFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }
  public static dev.xfj.protobuf.TransformComponent parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input);
  }
  public static dev.xfj.protobuf.TransformComponent parseDelimitedFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
  }
  public static dev.xfj.protobuf.TransformComponent parseFrom(
      com.google.protobuf.CodedInputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static dev.xfj.protobuf.TransformComponent parseFrom(
      com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }

  @java.lang.Override
  public Builder newBuilderForType() { return newBuilder(); }
  public static Builder newBuilder() {
    return DEFAULT_INSTANCE.toBuilder();
  }
  public static Builder newBuilder(dev.xfj.protobuf.TransformComponent prototype) {
    return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
  }
  @java.lang.Override
  public Builder toBuilder() {
    return this == DEFAULT_INSTANCE
        ? new Builder() : new Builder().mergeFrom(this);
  }

  @java.lang.Override
  protected Builder newBuilderForType(
      com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
    Builder builder = new Builder(parent);
    return builder;
  }
  /**
   * Protobuf type {@code dev.xfj.protobuf.TransformComponent}
   */
  public static final class Builder extends
      com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
      // @@protoc_insertion_point(builder_implements:dev.xfj.protobuf.TransformComponent)
      dev.xfj.protobuf.TransformComponentOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return dev.xfj.protobuf.SceneFile.internal_static_dev_xfj_protobuf_TransformComponent_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return dev.xfj.protobuf.SceneFile.internal_static_dev_xfj_protobuf_TransformComponent_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              dev.xfj.protobuf.TransformComponent.class, dev.xfj.protobuf.TransformComponent.Builder.class);
    }

    // Construct using dev.xfj.protobuf.TransformComponent.newBuilder()
    private Builder() {

    }

    private Builder(
        com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
      super(parent);

    }
    @java.lang.Override
    public Builder clear() {
      super.clear();
      bitField0_ = 0;
      translation_ = null;
      if (translationBuilder_ != null) {
        translationBuilder_.dispose();
        translationBuilder_ = null;
      }
      rotation_ = null;
      if (rotationBuilder_ != null) {
        rotationBuilder_.dispose();
        rotationBuilder_ = null;
      }
      scale_ = null;
      if (scaleBuilder_ != null) {
        scaleBuilder_.dispose();
        scaleBuilder_ = null;
      }
      return this;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.Descriptor
        getDescriptorForType() {
      return dev.xfj.protobuf.SceneFile.internal_static_dev_xfj_protobuf_TransformComponent_descriptor;
    }

    @java.lang.Override
    public dev.xfj.protobuf.TransformComponent getDefaultInstanceForType() {
      return dev.xfj.protobuf.TransformComponent.getDefaultInstance();
    }

    @java.lang.Override
    public dev.xfj.protobuf.TransformComponent build() {
      dev.xfj.protobuf.TransformComponent result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    @java.lang.Override
    public dev.xfj.protobuf.TransformComponent buildPartial() {
      dev.xfj.protobuf.TransformComponent result = new dev.xfj.protobuf.TransformComponent(this);
      if (bitField0_ != 0) { buildPartial0(result); }
      onBuilt();
      return result;
    }

    private void buildPartial0(dev.xfj.protobuf.TransformComponent result) {
      int from_bitField0_ = bitField0_;
      if (((from_bitField0_ & 0x00000001) != 0)) {
        result.translation_ = translationBuilder_ == null
            ? translation_
            : translationBuilder_.build();
      }
      if (((from_bitField0_ & 0x00000002) != 0)) {
        result.rotation_ = rotationBuilder_ == null
            ? rotation_
            : rotationBuilder_.build();
      }
      if (((from_bitField0_ & 0x00000004) != 0)) {
        result.scale_ = scaleBuilder_ == null
            ? scale_
            : scaleBuilder_.build();
      }
    }

    @java.lang.Override
    public Builder mergeFrom(com.google.protobuf.Message other) {
      if (other instanceof dev.xfj.protobuf.TransformComponent) {
        return mergeFrom((dev.xfj.protobuf.TransformComponent)other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(dev.xfj.protobuf.TransformComponent other) {
      if (other == dev.xfj.protobuf.TransformComponent.getDefaultInstance()) return this;
      if (other.hasTranslation()) {
        mergeTranslation(other.getTranslation());
      }
      if (other.hasRotation()) {
        mergeRotation(other.getRotation());
      }
      if (other.hasScale()) {
        mergeScale(other.getScale());
      }
      this.mergeUnknownFields(other.getUnknownFields());
      onChanged();
      return this;
    }

    @java.lang.Override
    public final boolean isInitialized() {
      return true;
    }

    @java.lang.Override
    public Builder mergeFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      if (extensionRegistry == null) {
        throw new java.lang.NullPointerException();
      }
      try {
        boolean done = false;
        while (!done) {
          int tag = input.readTag();
          switch (tag) {
            case 0:
              done = true;
              break;
            case 10: {
              input.readMessage(
                  getTranslationFieldBuilder().getBuilder(),
                  extensionRegistry);
              bitField0_ |= 0x00000001;
              break;
            } // case 10
            case 18: {
              input.readMessage(
                  getRotationFieldBuilder().getBuilder(),
                  extensionRegistry);
              bitField0_ |= 0x00000002;
              break;
            } // case 18
            case 26: {
              input.readMessage(
                  getScaleFieldBuilder().getBuilder(),
                  extensionRegistry);
              bitField0_ |= 0x00000004;
              break;
            } // case 26
            default: {
              if (!super.parseUnknownField(input, extensionRegistry, tag)) {
                done = true; // was an endgroup tag
              }
              break;
            } // default:
          } // switch (tag)
        } // while (!done)
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        throw e.unwrapIOException();
      } finally {
        onChanged();
      } // finally
      return this;
    }
    private int bitField0_;

    private dev.xfj.protobuf.Vector3f translation_;
    private com.google.protobuf.SingleFieldBuilderV3<
        dev.xfj.protobuf.Vector3f, dev.xfj.protobuf.Vector3f.Builder, dev.xfj.protobuf.Vector3fOrBuilder> translationBuilder_;
    /**
     * <code>.dev.xfj.protobuf.Vector3f translation = 1;</code>
     * @return Whether the translation field is set.
     */
    public boolean hasTranslation() {
      return ((bitField0_ & 0x00000001) != 0);
    }
    /**
     * <code>.dev.xfj.protobuf.Vector3f translation = 1;</code>
     * @return The translation.
     */
    public dev.xfj.protobuf.Vector3f getTranslation() {
      if (translationBuilder_ == null) {
        return translation_ == null ? dev.xfj.protobuf.Vector3f.getDefaultInstance() : translation_;
      } else {
        return translationBuilder_.getMessage();
      }
    }
    /**
     * <code>.dev.xfj.protobuf.Vector3f translation = 1;</code>
     */
    public Builder setTranslation(dev.xfj.protobuf.Vector3f value) {
      if (translationBuilder_ == null) {
        if (value == null) {
          throw new NullPointerException();
        }
        translation_ = value;
      } else {
        translationBuilder_.setMessage(value);
      }
      bitField0_ |= 0x00000001;
      onChanged();
      return this;
    }
    /**
     * <code>.dev.xfj.protobuf.Vector3f translation = 1;</code>
     */
    public Builder setTranslation(
        dev.xfj.protobuf.Vector3f.Builder builderForValue) {
      if (translationBuilder_ == null) {
        translation_ = builderForValue.build();
      } else {
        translationBuilder_.setMessage(builderForValue.build());
      }
      bitField0_ |= 0x00000001;
      onChanged();
      return this;
    }
    /**
     * <code>.dev.xfj.protobuf.Vector3f translation = 1;</code>
     */
    public Builder mergeTranslation(dev.xfj.protobuf.Vector3f value) {
      if (translationBuilder_ == null) {
        if (((bitField0_ & 0x00000001) != 0) &&
          translation_ != null &&
          translation_ != dev.xfj.protobuf.Vector3f.getDefaultInstance()) {
          getTranslationBuilder().mergeFrom(value);
        } else {
          translation_ = value;
        }
      } else {
        translationBuilder_.mergeFrom(value);
      }
      bitField0_ |= 0x00000001;
      onChanged();
      return this;
    }
    /**
     * <code>.dev.xfj.protobuf.Vector3f translation = 1;</code>
     */
    public Builder clearTranslation() {
      bitField0_ = (bitField0_ & ~0x00000001);
      translation_ = null;
      if (translationBuilder_ != null) {
        translationBuilder_.dispose();
        translationBuilder_ = null;
      }
      onChanged();
      return this;
    }
    /**
     * <code>.dev.xfj.protobuf.Vector3f translation = 1;</code>
     */
    public dev.xfj.protobuf.Vector3f.Builder getTranslationBuilder() {
      bitField0_ |= 0x00000001;
      onChanged();
      return getTranslationFieldBuilder().getBuilder();
    }
    /**
     * <code>.dev.xfj.protobuf.Vector3f translation = 1;</code>
     */
    public dev.xfj.protobuf.Vector3fOrBuilder getTranslationOrBuilder() {
      if (translationBuilder_ != null) {
        return translationBuilder_.getMessageOrBuilder();
      } else {
        return translation_ == null ?
            dev.xfj.protobuf.Vector3f.getDefaultInstance() : translation_;
      }
    }
    /**
     * <code>.dev.xfj.protobuf.Vector3f translation = 1;</code>
     */
    private com.google.protobuf.SingleFieldBuilderV3<
        dev.xfj.protobuf.Vector3f, dev.xfj.protobuf.Vector3f.Builder, dev.xfj.protobuf.Vector3fOrBuilder> 
        getTranslationFieldBuilder() {
      if (translationBuilder_ == null) {
        translationBuilder_ = new com.google.protobuf.SingleFieldBuilderV3<
            dev.xfj.protobuf.Vector3f, dev.xfj.protobuf.Vector3f.Builder, dev.xfj.protobuf.Vector3fOrBuilder>(
                getTranslation(),
                getParentForChildren(),
                isClean());
        translation_ = null;
      }
      return translationBuilder_;
    }

    private dev.xfj.protobuf.Vector3f rotation_;
    private com.google.protobuf.SingleFieldBuilderV3<
        dev.xfj.protobuf.Vector3f, dev.xfj.protobuf.Vector3f.Builder, dev.xfj.protobuf.Vector3fOrBuilder> rotationBuilder_;
    /**
     * <code>.dev.xfj.protobuf.Vector3f rotation = 2;</code>
     * @return Whether the rotation field is set.
     */
    public boolean hasRotation() {
      return ((bitField0_ & 0x00000002) != 0);
    }
    /**
     * <code>.dev.xfj.protobuf.Vector3f rotation = 2;</code>
     * @return The rotation.
     */
    public dev.xfj.protobuf.Vector3f getRotation() {
      if (rotationBuilder_ == null) {
        return rotation_ == null ? dev.xfj.protobuf.Vector3f.getDefaultInstance() : rotation_;
      } else {
        return rotationBuilder_.getMessage();
      }
    }
    /**
     * <code>.dev.xfj.protobuf.Vector3f rotation = 2;</code>
     */
    public Builder setRotation(dev.xfj.protobuf.Vector3f value) {
      if (rotationBuilder_ == null) {
        if (value == null) {
          throw new NullPointerException();
        }
        rotation_ = value;
      } else {
        rotationBuilder_.setMessage(value);
      }
      bitField0_ |= 0x00000002;
      onChanged();
      return this;
    }
    /**
     * <code>.dev.xfj.protobuf.Vector3f rotation = 2;</code>
     */
    public Builder setRotation(
        dev.xfj.protobuf.Vector3f.Builder builderForValue) {
      if (rotationBuilder_ == null) {
        rotation_ = builderForValue.build();
      } else {
        rotationBuilder_.setMessage(builderForValue.build());
      }
      bitField0_ |= 0x00000002;
      onChanged();
      return this;
    }
    /**
     * <code>.dev.xfj.protobuf.Vector3f rotation = 2;</code>
     */
    public Builder mergeRotation(dev.xfj.protobuf.Vector3f value) {
      if (rotationBuilder_ == null) {
        if (((bitField0_ & 0x00000002) != 0) &&
          rotation_ != null &&
          rotation_ != dev.xfj.protobuf.Vector3f.getDefaultInstance()) {
          getRotationBuilder().mergeFrom(value);
        } else {
          rotation_ = value;
        }
      } else {
        rotationBuilder_.mergeFrom(value);
      }
      bitField0_ |= 0x00000002;
      onChanged();
      return this;
    }
    /**
     * <code>.dev.xfj.protobuf.Vector3f rotation = 2;</code>
     */
    public Builder clearRotation() {
      bitField0_ = (bitField0_ & ~0x00000002);
      rotation_ = null;
      if (rotationBuilder_ != null) {
        rotationBuilder_.dispose();
        rotationBuilder_ = null;
      }
      onChanged();
      return this;
    }
    /**
     * <code>.dev.xfj.protobuf.Vector3f rotation = 2;</code>
     */
    public dev.xfj.protobuf.Vector3f.Builder getRotationBuilder() {
      bitField0_ |= 0x00000002;
      onChanged();
      return getRotationFieldBuilder().getBuilder();
    }
    /**
     * <code>.dev.xfj.protobuf.Vector3f rotation = 2;</code>
     */
    public dev.xfj.protobuf.Vector3fOrBuilder getRotationOrBuilder() {
      if (rotationBuilder_ != null) {
        return rotationBuilder_.getMessageOrBuilder();
      } else {
        return rotation_ == null ?
            dev.xfj.protobuf.Vector3f.getDefaultInstance() : rotation_;
      }
    }
    /**
     * <code>.dev.xfj.protobuf.Vector3f rotation = 2;</code>
     */
    private com.google.protobuf.SingleFieldBuilderV3<
        dev.xfj.protobuf.Vector3f, dev.xfj.protobuf.Vector3f.Builder, dev.xfj.protobuf.Vector3fOrBuilder> 
        getRotationFieldBuilder() {
      if (rotationBuilder_ == null) {
        rotationBuilder_ = new com.google.protobuf.SingleFieldBuilderV3<
            dev.xfj.protobuf.Vector3f, dev.xfj.protobuf.Vector3f.Builder, dev.xfj.protobuf.Vector3fOrBuilder>(
                getRotation(),
                getParentForChildren(),
                isClean());
        rotation_ = null;
      }
      return rotationBuilder_;
    }

    private dev.xfj.protobuf.Vector3f scale_;
    private com.google.protobuf.SingleFieldBuilderV3<
        dev.xfj.protobuf.Vector3f, dev.xfj.protobuf.Vector3f.Builder, dev.xfj.protobuf.Vector3fOrBuilder> scaleBuilder_;
    /**
     * <code>.dev.xfj.protobuf.Vector3f scale = 3;</code>
     * @return Whether the scale field is set.
     */
    public boolean hasScale() {
      return ((bitField0_ & 0x00000004) != 0);
    }
    /**
     * <code>.dev.xfj.protobuf.Vector3f scale = 3;</code>
     * @return The scale.
     */
    public dev.xfj.protobuf.Vector3f getScale() {
      if (scaleBuilder_ == null) {
        return scale_ == null ? dev.xfj.protobuf.Vector3f.getDefaultInstance() : scale_;
      } else {
        return scaleBuilder_.getMessage();
      }
    }
    /**
     * <code>.dev.xfj.protobuf.Vector3f scale = 3;</code>
     */
    public Builder setScale(dev.xfj.protobuf.Vector3f value) {
      if (scaleBuilder_ == null) {
        if (value == null) {
          throw new NullPointerException();
        }
        scale_ = value;
      } else {
        scaleBuilder_.setMessage(value);
      }
      bitField0_ |= 0x00000004;
      onChanged();
      return this;
    }
    /**
     * <code>.dev.xfj.protobuf.Vector3f scale = 3;</code>
     */
    public Builder setScale(
        dev.xfj.protobuf.Vector3f.Builder builderForValue) {
      if (scaleBuilder_ == null) {
        scale_ = builderForValue.build();
      } else {
        scaleBuilder_.setMessage(builderForValue.build());
      }
      bitField0_ |= 0x00000004;
      onChanged();
      return this;
    }
    /**
     * <code>.dev.xfj.protobuf.Vector3f scale = 3;</code>
     */
    public Builder mergeScale(dev.xfj.protobuf.Vector3f value) {
      if (scaleBuilder_ == null) {
        if (((bitField0_ & 0x00000004) != 0) &&
          scale_ != null &&
          scale_ != dev.xfj.protobuf.Vector3f.getDefaultInstance()) {
          getScaleBuilder().mergeFrom(value);
        } else {
          scale_ = value;
        }
      } else {
        scaleBuilder_.mergeFrom(value);
      }
      bitField0_ |= 0x00000004;
      onChanged();
      return this;
    }
    /**
     * <code>.dev.xfj.protobuf.Vector3f scale = 3;</code>
     */
    public Builder clearScale() {
      bitField0_ = (bitField0_ & ~0x00000004);
      scale_ = null;
      if (scaleBuilder_ != null) {
        scaleBuilder_.dispose();
        scaleBuilder_ = null;
      }
      onChanged();
      return this;
    }
    /**
     * <code>.dev.xfj.protobuf.Vector3f scale = 3;</code>
     */
    public dev.xfj.protobuf.Vector3f.Builder getScaleBuilder() {
      bitField0_ |= 0x00000004;
      onChanged();
      return getScaleFieldBuilder().getBuilder();
    }
    /**
     * <code>.dev.xfj.protobuf.Vector3f scale = 3;</code>
     */
    public dev.xfj.protobuf.Vector3fOrBuilder getScaleOrBuilder() {
      if (scaleBuilder_ != null) {
        return scaleBuilder_.getMessageOrBuilder();
      } else {
        return scale_ == null ?
            dev.xfj.protobuf.Vector3f.getDefaultInstance() : scale_;
      }
    }
    /**
     * <code>.dev.xfj.protobuf.Vector3f scale = 3;</code>
     */
    private com.google.protobuf.SingleFieldBuilderV3<
        dev.xfj.protobuf.Vector3f, dev.xfj.protobuf.Vector3f.Builder, dev.xfj.protobuf.Vector3fOrBuilder> 
        getScaleFieldBuilder() {
      if (scaleBuilder_ == null) {
        scaleBuilder_ = new com.google.protobuf.SingleFieldBuilderV3<
            dev.xfj.protobuf.Vector3f, dev.xfj.protobuf.Vector3f.Builder, dev.xfj.protobuf.Vector3fOrBuilder>(
                getScale(),
                getParentForChildren(),
                isClean());
        scale_ = null;
      }
      return scaleBuilder_;
    }
    @java.lang.Override
    public final Builder setUnknownFields(
        final com.google.protobuf.UnknownFieldSet unknownFields) {
      return super.setUnknownFields(unknownFields);
    }

    @java.lang.Override
    public final Builder mergeUnknownFields(
        final com.google.protobuf.UnknownFieldSet unknownFields) {
      return super.mergeUnknownFields(unknownFields);
    }


    // @@protoc_insertion_point(builder_scope:dev.xfj.protobuf.TransformComponent)
  }

  // @@protoc_insertion_point(class_scope:dev.xfj.protobuf.TransformComponent)
  private static final dev.xfj.protobuf.TransformComponent DEFAULT_INSTANCE;
  static {
    DEFAULT_INSTANCE = new dev.xfj.protobuf.TransformComponent();
  }

  public static dev.xfj.protobuf.TransformComponent getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static final com.google.protobuf.Parser<TransformComponent>
      PARSER = new com.google.protobuf.AbstractParser<TransformComponent>() {
    @java.lang.Override
    public TransformComponent parsePartialFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      Builder builder = newBuilder();
      try {
        builder.mergeFrom(input, extensionRegistry);
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        throw e.setUnfinishedMessage(builder.buildPartial());
      } catch (com.google.protobuf.UninitializedMessageException e) {
        throw e.asInvalidProtocolBufferException().setUnfinishedMessage(builder.buildPartial());
      } catch (java.io.IOException e) {
        throw new com.google.protobuf.InvalidProtocolBufferException(e)
            .setUnfinishedMessage(builder.buildPartial());
      }
      return builder.buildPartial();
    }
  };

  public static com.google.protobuf.Parser<TransformComponent> parser() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.protobuf.Parser<TransformComponent> getParserForType() {
    return PARSER;
  }

  @java.lang.Override
  public dev.xfj.protobuf.TransformComponent getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

}
