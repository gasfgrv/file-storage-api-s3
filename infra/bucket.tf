resource "aws_s3_bucket" "s3_spring_bucket" {
  bucket        = "teste-spring-bucket"
  force_destroy = true
}

resource "aws_s3_bucket_public_access_block" "s3_spring_bucket_block" {
  bucket              = aws_s3_bucket.s3_spring_bucket.id
  block_public_acls   = true
  block_public_policy = true
  ignore_public_acls  = true
}