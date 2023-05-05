data "aws_iam_policy_document" "s3_access_bucket_policy_document" {
  version = "2012-10-17"
  statement {
    effect    = "Allow"
    actions   = ["s3:PutObject", "s3:GetObject", "s3:ListObject", "s3:DeleteObject"]
    resources = [aws_s3_bucket.s3_spring_bucket.arn]
  }
}

resource "aws_iam_policy" "s3_access_bucket_policy" {
  name        = "S3springAccessBucket"
  description = "Acesso ao bucket para poder fazer o download/upload dos arquivos"
  path        = "/"
  policy      = data.aws_iam_policy_document.s3_access_bucket_policy_document.json
}